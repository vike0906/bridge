package com.vike.bridge.dao;

import com.vike.bridge.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author: lsl
 * @createDate: 2019/11/26
 */
public interface SysUserRepository extends JpaRepository<SysUser,Long>, JpaSpecificationExecutor<SysUser> {

    @Query(value = "select * from b_sys_user where login_name = ?1 and is_delete = 1", nativeQuery = true)
    Optional<SysUser> findSysUserByLoginName(String loginName);
}
