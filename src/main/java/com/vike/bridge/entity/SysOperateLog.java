package com.vike.bridge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/12/4
 */
@Entity
@Table(name = "b_sys_operate_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysOperateLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "req_type")
    private int requestType;

    @Column(name = "req_name")
    private String requestName;

    @Column(name="req_param")
    private String requestParam;

    @Column(name = "rsp_code")
    private int responseCode;

    @Column(name = "rsp_message")
    private String responseMessage;

    @Column(name="ip_addr")
    private int ipAddr;

    @Column(name = "create_time")
    private Date createTime;

}
