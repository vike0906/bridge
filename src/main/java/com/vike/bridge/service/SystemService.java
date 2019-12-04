package com.vike.bridge.service;

import com.vike.bridge.common.PageLimit;
import com.vike.bridge.entity.SysAction;
import com.vike.bridge.entity.SysRole;
import com.vike.bridge.entity.SysUser;
import com.vike.bridge.vo.ActionVo;
import com.vike.bridge.vo.TreeDataVo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/11/29
 */
public interface SystemService {

    Page<SysUser> getUsers(String queryStr, String order, String direction, PageLimit pageLimit);

    String saveUser(Long id, String name, String loginName, Long roleId ,Integer status);

    void deleteUser(Long id);

    List<SysRole> activeRole();


    Page<SysRole> getRoles(String order, String direction, PageLimit pageLimit);

    String saveRole(Long id, String name, String code, Integer status,Long[] actionIds);

    void deleteRole(Long id);

    TreeDataVo activeAction(Long roleId);


    Page<SysAction> getActions(String order, String direction, PageLimit pageLimit);

    String saveAction(Long id, String name, Integer type, Long parentId, String icon, String url, Integer sort);

    void deleteAction(Long id);

    List<SysAction> activeParentAction();

    ActionVo[] roleMenu(Long roleId);

}
