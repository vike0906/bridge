package com.vike.bridge.service.impl;

import com.vike.bridge.common.*;
import com.vike.bridge.config.shiro.AuthUtil;
import com.vike.bridge.dao.SysActionRepository;
import com.vike.bridge.dao.SysRoleActionRepository;
import com.vike.bridge.dao.SysRoleRepository;
import com.vike.bridge.dao.SysUserRepository;
import com.vike.bridge.entity.SysAction;
import com.vike.bridge.entity.SysRole;
import com.vike.bridge.entity.SysRoleAction;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.service.SystemService;
import com.vike.bridge.utils.RandomUtil;
import com.vike.bridge.vo.ActionVo;
import com.vike.bridge.vo.TreeDataVo;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.util.*;

/**
 * @author: lsl
 * @createDate: 2019/11/29
 */
@Service
public class SystemServiceImpl implements SystemService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    SysUserRepository sysUserRepository;
    @Autowired
    SysRoleRepository sysRoleRepository;
    @Autowired
    SysActionRepository sysActionRepository;
    @Autowired
    SysRoleActionRepository sysRoleActionRepository;

    @Override
    public Page<SysUser> getUsers(String queryStr, String order, String direction, PageLimit pageLimit) {
        Specification<SysUser> specification = (Specification<SysUser>)(r,q,b)->{

            List<Predicate> list = new ArrayList<>();

            if(!StringUtils.isEmpty(queryStr)){
                Path<String> namePath = r.get("name");
                Path<String> loginNamePath = r.get("loginName");
                list.add(b.or(b.like(namePath, queryStr), b.like(loginNamePath, queryStr)));
            }

            list.add(b.notEqual(r.get("isDelete"), GlobalConstant.ABNORMAL));

            Predicate [] predicates = new Predicate[list.size()];
            q.where(b.and(list.toArray(predicates)));

            if(!StringUtils.isEmpty(order)){
                if("ascend".equals(direction)){
                    q.orderBy(b.asc(r.get(order)));
                }else if("descend".equals(direction)){
                    q.orderBy(b.desc(r.get(order)));
                }
            }
            return q.getRestriction();
        };
        Page<SysUser> page = sysUserRepository.findAll(specification, pageLimit.page());
        Session unwrap = entityManager.unwrap(Session.class);
        List<SysUser> content = page.getContent();
        for(SysUser sysUser:content){
            if(sysUser.getId()==3L)
            sysUser.setPassword("").setSalt("");
            unwrap.evict(sysUser);
        }
        return new PageImpl<>(content,page.getPageable(),page.getTotalElements());
    }

    @Override
    public String saveUser(Long id, String name, String loginName, Long roleId, Integer status) {
        Optional<SysRole> roleOp = sysRoleRepository.findById(roleId);
        Assert.check(!roleOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
        SysRole sysRole = roleOp.get();
        Assert.check(sysRole.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
        if(id.intValue()<0){
            //新增
            Optional<SysUser> loginOp = sysUserRepository.findSysUserByLoginName(loginName);
            Assert.check(loginOp.isPresent(),ExceptionEnum.LOGIN_NAME_EXIST);
            SysUser sysUser = new SysUser();
            String salt = RandomUtil.randomString(GlobalConstant.SALT_LENGTH);
            String psd = AuthUtil.hash(GlobalConstant.INIT_PSD,salt);
            sysUser.setName(name).setLoginName(loginName).setStatus(status).setIsDelete(GlobalConstant.NORMAL)
                    .setSalt(salt).setPassword(psd).setRole(sysRole);
            sysUserRepository.save(sysUser);
            return "用户添加成功";
        }else{
            //修改
            Optional<SysUser> userOp = sysUserRepository.findById(id);
            Assert.check(!userOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
            SysUser sysUser = userOp.get();
            Assert.check(sysUser.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
            sysUser.setName(name).setRole(sysRole).setStatus(status);
            sysUserRepository.save(sysUser);
            if(status==GlobalConstant.ABNORMAL){
                AuthUtil.remove(id);
            }
            return "用户修改成功";
        }
    }

    @Override
    public void deleteUser(Long id) {
        Optional<SysUser> userOp = sysUserRepository.findById(id);
        Assert.check(!userOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
        SysUser sysUser = userOp.get();
        Assert.check(sysUser.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
        sysUser.setIsDelete(GlobalConstant.ABNORMAL);
        sysUserRepository.save(sysUser);
        AuthUtil.remove(id);
    }

    @Override
    public List<SysRole> activeRole() {
        return sysRoleRepository.findAllByStatusAndIsDelete(GlobalConstant.NORMAL,GlobalConstant.NORMAL);
    }

    @Override
    public Page<SysRole> getRoles(String order, String direction, PageLimit pageLimit) {
        Specification<SysRole> specification = (Specification<SysRole>)(r,q,b)->{

            List<Predicate> list = new ArrayList<>();

            list.add(b.notEqual(r.get("isDelete"), GlobalConstant.ABNORMAL));

            Predicate [] predicates = new Predicate[list.size()];
            q.where(b.and(list.toArray(predicates)));

            if(!StringUtils.isEmpty(order)){
                if("ascend".equals(direction)){
                    q.orderBy(b.asc(r.get(order)));
                }else if("descend".equals(direction)){
                    q.orderBy(b.desc(r.get(order)));
                }
            }
            return q.getRestriction();
        };
        return sysRoleRepository.findAll(specification, pageLimit.page());
    }

    @Override
    @Transactional
    public String saveRole(Long id, String name, String code, Integer status, Long[] actionIds) {
        if(id.intValue()<0){
            //新增
            SysRole sysRole = new SysRole();
            sysRole.setName(name).setCode(code).setStatus(status).setIsDelete(GlobalConstant.NORMAL);
            SysRole save = sysRoleRepository.save(sysRole);
            dealRoleAction(save.getId(),actionIds);
            return "角色添加成功";
        }else{
            //修改
            Optional<SysRole> roleOp = sysRoleRepository.findById(id);
            Assert.check(!roleOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
            SysRole sysRole = roleOp.get();
            Assert.check(sysRole.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
            sysRole.setName(name).setCode(code).setStatus(status);
            sysRoleRepository.save(sysRole);
            dealRoleAction(id,actionIds);
            return "角色修改成功";
        }
    }

    @Override
    public void deleteRole(Long id) {
        Optional<SysRole> roleOp = sysRoleRepository.findById(id);
        Assert.check(!roleOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
        SysRole sysRole = roleOp.get();
        Assert.check(sysRole.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
        sysRole.setIsDelete(GlobalConstant.ABNORMAL);
        sysRoleRepository.save(sysRole);
    }

    @Override
    public TreeDataVo activeAction(Long roleId) {

        List<SysAction> activeActions = sysActionRepository.findAllByStatusAndIsDelete(GlobalConstant.NORMAL, GlobalConstant.NORMAL);

        TreeDataVo treeDataVo = SystemHelp.actions2TreeData(activeActions);

        if(roleId!=null){

            List<SysRoleAction> activeRoleAction = sysRoleActionRepository.findAllByRoleIdAndStatus(roleId, GlobalConstant.NORMAL);

            String[] checkedKeys = SystemHelp.clearParentAction(activeActions, activeRoleAction);

            treeDataVo.setCheckedKeys(checkedKeys);
        }

        return treeDataVo;
    }

    @Override
    public Page<SysAction> getActions(String order, String direction, PageLimit pageLimit) {

        Specification<SysAction> specification = (Specification<SysAction>)(r,q,b)->{

            List<Predicate> list = new ArrayList<>();

            list.add(b.notEqual(r.get("isDelete"), GlobalConstant.ABNORMAL));

            Predicate [] predicates = new Predicate[list.size()];
            q.where(b.and(list.toArray(predicates)));

            List<Order> orderList = new ArrayList<>();
            orderList.add(b.asc(r.get("parentId")));
            orderList.add(b.asc(r.get("sort")));

            if(!StringUtils.isEmpty(order)){
                if("ascend".equals(direction)){
                    orderList.add(b.asc(r.get(order)));
                }else if("descend".equals(direction)){
                    orderList.add(b.desc(r.get(order)));
                }
            }
            q.orderBy(orderList);
            return q.getRestriction();
        };
        return sysActionRepository.findAll(specification, pageLimit.page());
    }

    @Override
    public String saveAction(Long id, String name, Integer type, Long parentId, String icon, String url, Integer sort) {
        if(id.intValue()<0){
            //新增
            SysAction sysAction = new SysAction();
            sysAction.setName(name).setIcon(icon).setUrl(url).setType(type).setParentId(parentId).setSort(sort)
                    .setIsDelete(GlobalConstant.NORMAL).setStatus(GlobalConstant.NORMAL);
            sysActionRepository.save(sysAction);
            return "权限添加成功";
        }else{
            //修改
            Optional<SysAction> actionOp = sysActionRepository.findById(id);
            Assert.check(!actionOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
            SysAction sysAction = actionOp.get();
            Assert.check(sysAction.getStatus()==GlobalConstant.ABNORMAL||sysAction.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
            sysAction.setName(name).setIcon(icon).setUrl(url).setSort(sort);
            sysActionRepository.save(sysAction);
            return "权限修改成功";
        }
    }


    @Override
    @Transactional
    public void deleteAction(Long id) {
        Optional<SysAction> actionOp = sysActionRepository.findById(id);
        Assert.check(!actionOp.isPresent(),ExceptionEnum.OPTIONAL_ERROR);
        SysAction sysAction = actionOp.get();
        Assert.check(sysAction.getIsDelete()==GlobalConstant.ABNORMAL,ExceptionEnum.OPTIONAL_DELETE);
        sysAction.setIsDelete(GlobalConstant.ABNORMAL);
        sysActionRepository.save(sysAction);
        sysRoleActionRepository.deleteByActionId(id);
    }

    @Override
    public List<SysAction> activeParentAction() {
        return sysActionRepository.findAllByStatusAndIsDeleteAndType(GlobalConstant.NORMAL,GlobalConstant.NORMAL,GlobalConstant.FIRST_MENU);
    }

    @Override
    public ActionVo[] roleMenu(Long roleId) {

        List<SysAction> actionsByRole = sysActionRepository.findActionsByRole(roleId);

        return SystemHelp.createMenu(actionsByRole);
    }

    /**处理角色权限*/
    private void dealRoleAction(Long roleId, Long[] actions){

        List<SysAction> activeActionList = sysActionRepository.findAllByStatusAndIsDelete(GlobalConstant.NORMAL, GlobalConstant.NORMAL);
        Map<Long,SysAction> activeActionMap = new HashMap<>(activeActionList.size());
        Map<Long,Integer> roleActionStatusMap = new HashMap<>(activeActionList.size());

        for(SysAction sysAction:activeActionList){
            activeActionMap.put(sysAction.getId(),sysAction);
            roleActionStatusMap.put(sysAction.getId(),GlobalConstant.ABNORMAL);
        }

        for(Long actionId:actions){
            SysAction sysAction = activeActionMap.get(actionId);
            if(sysAction!=null&&sysAction.getParentId()!=0){
                roleActionStatusMap.put(sysAction.getId(),GlobalConstant.NORMAL);
                roleActionStatusMap.put(sysAction.getParentId(),GlobalConstant.NORMAL);
            }
        }

        Iterator<Map.Entry<Long, Integer>> iterator = roleActionStatusMap.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<Long, Integer> roleActionStatus = iterator.next();
            Optional<SysRoleAction> op = sysRoleActionRepository.findByRoleIdAndActionId(roleId, roleActionStatus.getKey());
            if(op.isPresent()){
                SysRoleAction sysRoleAction = op.get();
                if(sysRoleAction.getStatus()!=roleActionStatus.getValue()){
                    sysRoleAction.setStatus(roleActionStatus.getValue());
                    sysRoleActionRepository.save(sysRoleAction);   
                }
            }else {
                SysRoleAction sysRoleAction = new SysRoleAction();
                sysRoleAction.setRoleId(roleId).setActionId(roleActionStatus.getKey()).setStatus(roleActionStatus.getValue());
                sysRoleActionRepository.save(sysRoleAction);
            }
        }
    }
}
