package com.vike.bridge.vo;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/12/7
 */
@Data
@Entity
public class RequestStatisticsVo {

    @Id
    @Column(name = "_date")
    private Date date;

    @Column(name = "_count")
    private Long count;
}
