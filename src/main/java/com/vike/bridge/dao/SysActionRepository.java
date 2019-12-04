package com.vike.bridge.dao;

import com.vike.bridge.entity.SysAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


/**
 * @author: lsl
 * @createDate: 2019/11/26
 */
public interface SysActionRepository extends JpaRepository<SysAction,Long>, JpaSpecificationExecutor<SysAction> {

    @Query(value = "SELECT b.* FROM b_sys_role_action a LEFT JOIN b_sys_action b ON a.action_id = b.id WHERE a.`status` = 1 AND a.role_id = ?1 AND b.`status` = 1 AND b.`is_delete` = 1", nativeQuery = true)
    List<SysAction> findActionsByRole(long roleId);

    List<SysAction> findAllByStatusAndIsDelete(int status, int isDelete);

    List<SysAction> findAllByStatusAndIsDeleteAndType(int status, int isDelete,int type);
}