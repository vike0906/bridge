package com.vike.bridge.dao;

import com.vike.bridge.entity.SysRoleAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


/**
 * @author: lsl
 * @createDate: 2019/11/26
 */
public interface SysRoleActionRepository extends JpaRepository<SysRoleAction,Long>, JpaSpecificationExecutor<SysRoleAction> {

    Optional<SysRoleAction> findByRoleIdAndActionId(long roleId, long actionId);

    List<SysRoleAction> findAllByRoleIdAndStatus(long roleId, int status);

    @Modifying
    @Query(value = "delete from b_sys_role_action where action_id = ?1",nativeQuery = true)
    void deleteByActionId(Long actionId);
}
