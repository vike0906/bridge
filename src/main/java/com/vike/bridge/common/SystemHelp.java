package com.vike.bridge.common;

import com.vike.bridge.entity.SysAction;
import com.vike.bridge.entity.SysRoleAction;
import com.vike.bridge.vo.ActionVo;
import com.vike.bridge.vo.TreeDataVo;
import com.vike.bridge.vo.TreeNodeVo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: lsl
 * @createDate: 2019/11/30
 */
public class SystemHelp {

    /**
     * @description: 构造树形菜单数据结构
     * @params [actions]
     * @return TreeDataVo
     */
    public static TreeDataVo actions2TreeData(List<SysAction> actions){

        List<TreeNodeVo> tree = new ArrayList<>();
        List<TreeNodeVo> second = new ArrayList<>();
        TreeNodeVo menu = new TreeNodeVo();

        List<SysAction> allActions = actions.stream().sorted(Comparator.comparingInt(SysAction::getSort)).collect(Collectors.toList());

        Map<Long, List<TreeNodeVo>> actionMap = allActions.stream().filter(a -> a.getStatus() != 0).map(a->{
            TreeNodeVo node = new TreeNodeVo();
            node.setTitle(a.getName()).setKey(String.valueOf(a.getId())).setParentId(a.getParentId());
            return node;
        }).collect(Collectors.groupingBy(TreeNodeVo::getParentId));

        List<SysAction> parents = allActions.stream().filter(a -> a.getType() == GlobalConstant.FIRST_MENU).collect(Collectors.toList());

        for(int i=0;i<parents.size();i++){
            SysAction parent = parents.get(i);
            TreeNodeVo secondMenu = new TreeNodeVo();
            secondMenu.setTitle(parent.getName()).setKey(String.valueOf(parent.getId())).setChildren(actionMap.get(parent.getId()));
            second.add(secondMenu);
        }
        String[] expandedKeys = {"0"};
        menu.setTitle("菜单权限").setKey("0").setChildren(second);
        tree.add(menu);

        TreeDataVo treeData = new TreeDataVo();
        treeData.setTree(tree).setExpandedKeys(expandedKeys);

        return treeData;
    }

    /**
     * @description: 清除一级菜单
     * @params [activeActions, activeRoleAction]
     * @return java.lang.String[]
     */
    public static String[] clearParentAction(List<SysAction> activeActions, List<SysRoleAction> activeRoleAction){

        List<Long> parentIds = new ArrayList<>();

        for(SysAction action:activeActions){
            if(action.getType()==GlobalConstant.FIRST_MENU){
                parentIds.add(action.getId());
            }
        }

        List<SysRoleAction> activeRoleActionWithoutParent = new ArrayList<>();

        for(SysRoleAction roleAction:activeRoleAction){
            if(!parentIds.contains(roleAction.getActionId())){
                activeRoleActionWithoutParent.add(roleAction);
            }
        }

        String [] checkedKeys = new String [activeRoleActionWithoutParent.size()];

        for(int i =0;i<activeRoleActionWithoutParent.size();i++){
            checkedKeys[i] = String.valueOf(activeRoleActionWithoutParent.get(i).getActionId());
        }

        return checkedKeys;
    }
    /**
     * @description: 清除一级菜单
     * @params actions
     * @return Action[]
     */
    public static ActionVo[] createMenu(List<SysAction> actions){

        Map<Long, List<ActionVo>> subActionMap = actions.stream().filter(a -> a.getType() == GlobalConstant.SECOND_MENU)
                .sorted(Comparator.comparingInt(SysAction::getSort))
                .map(a -> {
                    ActionVo actionVo = new ActionVo();
                    actionVo.setId(a.getId()).setParentId(a.getParentId()).setName(a.getName()).setUrl(a.getUrl());
                    return actionVo;
                })
                .collect(Collectors.groupingBy(ActionVo::getParentId));

        return actions.stream()
                .filter(a -> a.getType() == GlobalConstant.FIRST_MENU)
                .sorted(Comparator.comparingInt(SysAction::getSort))
                .map(a -> {
                    ActionVo actionVo = new ActionVo();
                    List<ActionVo> actionVos = subActionMap.get(a.getId());
                    if(actionVos==null){
                        actionVos = new ArrayList<>(0);
                    }
                    ActionVo[] actionVoArray = new ActionVo[actionVos.size()];
                    actionVo.setId(a.getId()).setName(a.getName()).setIcon(a.getIcon()).setSubAction(actionVos.toArray(actionVoArray));
                    return actionVo;
                }).toArray(ActionVo[]::new);
    }
}
