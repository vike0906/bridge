package com.vike.bridge.controller;

import com.vike.bridge.common.ApiPointcut;
import com.vike.bridge.common.CommonResponse;
import com.vike.bridge.common.PageLimit;
import com.vike.bridge.common.SystemHelp;
import com.vike.bridge.config.shiro.AuthUtil;
import com.vike.bridge.entity.SysAction;
import com.vike.bridge.entity.SysRole;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.service.SystemService;
import com.vike.bridge.vo.ActionVo;
import com.vike.bridge.vo.TreeDataVo;
import com.vike.bridge.vo.TreeNodeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/11/29
 */
@Slf4j
@RestController
@RequestMapping("system")
public class SystemController {
    @Autowired
    SystemService systemService;

    @GetMapping("users")
    public CommonResponse<Page<SysUser>> users(@RequestParam(required = false) String queryStr,
                                               @RequestParam(required = false) String order,
                                               @RequestParam(required = false) String direction,
                                               PageLimit pageLimit){
        if(!StringUtils.isEmpty(queryStr)){
            queryStr = "%"+queryStr+"%";
        }
        Page<SysUser> page = systemService.getUsers(queryStr,order,direction,pageLimit);
        return new CommonResponse<>(page);
    }

    @ApiPointcut("添加/编辑用户")
    @PostMapping("save-user")
    public CommonResponse saveUser(@RequestParam(required = false) Long id,
                                   @RequestParam String name,
                                   @RequestParam String loginName,
                                   @RequestParam Long roleId ,
                                   @RequestParam Integer status){
        String s = systemService.saveUser(id,name,loginName,roleId,status);
        return CommonResponse.success(s);

    }

    @ApiPointcut("删除用户")
    @DeleteMapping("delete-user")
    public CommonResponse saveUser(@RequestParam Long id){
        systemService.deleteUser(id);
        return CommonResponse.success("删除成功");
    }

    @GetMapping("active-role")
    public CommonResponse<List<SysRole>> activeRole(){
        List<SysRole> sysRoles = systemService.activeRole();
        return new CommonResponse<>(sysRoles);
    }


    @GetMapping("roles")
    public CommonResponse<Page<SysRole>> roles(@RequestParam(required = false) String order,
                                               @RequestParam(required = false) String direction,
                                               PageLimit pageLimit){
        Page<SysRole> page = systemService.getRoles(order,direction,pageLimit);
        return new CommonResponse<>(page);
    }

    @ApiPointcut("添加/编辑角色")
    @PostMapping("save-role")
    public CommonResponse saveRole(@RequestParam(required = false) Long id,
                                   @RequestParam String name,
                                   @RequestParam String code,
                                   @RequestParam Integer status,
                                   @RequestParam(value = "actions[]") String [] actions){
        Long [] actionIds = new Long[actions.length];
        if(actions.length>0){
            for(int i=0;i<actions.length;i++){
                actionIds[i] = Long.valueOf(actions[i]);
            }
        }
        String s = systemService.saveRole(id,name,code,status,actionIds);
        return CommonResponse.success(s);

    }

    @ApiPointcut("删除角色")
    @DeleteMapping("delete-role")
    public CommonResponse deleteRole(@RequestParam Long id){
        systemService.deleteRole(id);
        return CommonResponse.success("删除成功");
    }

    @GetMapping("active-action")
    public CommonResponse<TreeDataVo> activeAction(@RequestParam(required = false)Long roleId){
        TreeDataVo treeData = systemService.activeAction(roleId);
        return new CommonResponse<>(treeData);
    }

    @GetMapping("actions")
    public CommonResponse<Page<SysAction>> actions(@RequestParam(required = false) String order,
                                               @RequestParam(required = false) String direction,
                                               PageLimit pageLimit){
        Page<SysAction> page = systemService.getActions(order,direction,pageLimit);
        return new CommonResponse<>(page);
    }

    @ApiPointcut("添加/编辑权限")
    @PostMapping("save-action")
    public CommonResponse saveAction(@RequestParam(required = false) Long id,
                                     @RequestParam String name,
                                     @RequestParam Integer type,
                                     @RequestParam Long parentId,
                                     @RequestParam String icon,
                                     @RequestParam String url,
                                     @RequestParam Integer sort){
        String s = systemService.saveAction(id,name,type,parentId,icon,url,sort);
        return CommonResponse.success(s);
    }

    @ApiPointcut("删除权限")
    @DeleteMapping("delete-action")
    public CommonResponse deleteAction(@RequestParam Long id){
        systemService.deleteAction(id);
        return CommonResponse.success("删除成功");
    }

    @GetMapping("active-parent-action")
    public CommonResponse<List<SysAction>> activeParentAction(){
        List<SysAction> sysActions = systemService.activeParentAction();
        return new CommonResponse<>(sysActions);
    }

    @GetMapping("menu")
    public CommonResponse<ActionVo[]> menu(){

        SysUser user = AuthUtil.getUser();

        ActionVo[] actionVos = systemService.roleMenu(user.getRole().getId());

        return new CommonResponse<>(actionVos);
    }

}
