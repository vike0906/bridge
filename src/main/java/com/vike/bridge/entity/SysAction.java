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
@Table(name = "b_sys_action")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class SysAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private int type;

    private String icon;

    private String url;

    @Column(name = "parent_id")
    private long parentId;

    private int sort;

    private int status;

    @Column(name = "is_delete")
    private int isDelete;

    @Column(name = "create_time")
    private Date createTime;



}
