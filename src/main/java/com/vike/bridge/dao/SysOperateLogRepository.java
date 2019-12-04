package com.vike.bridge.dao;

import com.vike.bridge.entity.SysOperateLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author: lsl
 * @createDate: 2019/12/4
 */
public interface SysOperateLogRepository extends JpaRepository<SysOperateLog,Long>, JpaSpecificationExecutor<SysOperateLog> {

}
