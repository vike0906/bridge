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
@Table(name = "b_sys_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "login_name")
    private String loginName;

    private String password;

    private String salt;

    @Column(name = "auth_token")
    private String authToken;

    @ManyToOne
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private SysRole role;

    private int status;

    @Column(name = "is_delete")
    private int isDelete;

    @Column(name = "create_time")
    private Date createTime;

}
