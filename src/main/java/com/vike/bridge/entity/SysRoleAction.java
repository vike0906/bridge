package com.vike.bridge.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: lsl
 * @CreateDate: 2019/10/29
 */
@Entity
@Table(name = "b_sys_role_action")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysRoleAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "role_id")
    private long roleId;

    @Column(name = "action_id")
    private long actionId;

    private int status;

    @Column(name = "create_time")
    private Date createTime;

}
