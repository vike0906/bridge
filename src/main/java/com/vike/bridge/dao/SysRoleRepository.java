package com.vike.bridge.dao;

import com.vike.bridge.entity.SysRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


/**
 * @author: lsl
 * @createDate: 2019/11/26
 */
public interface SysRoleRepository extends JpaRepository<SysRole,Long>, JpaSpecificationExecutor<SysRole> {

    List<SysRole> findAllByStatusAndIsDelete(int status,int isDelete);
}
