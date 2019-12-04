package com.vike.bridge.mogoEntity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author: lsl
 * @createDate: 2019/9/23
 */
@Document(collection = "baseStockInfo")
/*@CompoundIndexes({
        @CompoundIndex(name = "name_code",def = "{'code':1,'name':1}",unique = true)
})*/
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class BaseStockInfo {

    @Id
    private String id;

    /**股票代码*/
    @Field("code")
    @Indexed(unique = true)
    private String code;

    /**股票名称*/
    @Field("name")
    @Indexed()
    private String name;

    /**交易所*/
    @Field("exchange")
    private String exchange;

    /**最新价*/
    @Field("last_price")
    private double lastPrice;

    /**涨跌幅*/
    @Field("change")
    private double change;

    /**60日涨跌幅*/
    @Field("change_sixty")
    private double changeSixty;

    /**年初至今涨跌幅*/
    @Field("change_begin_year")
    private double changeBeginYear;

    /**涨跌额度*/
    @Field("change_amount")
    private double changeAmount;

    /**换手率*/
    @Field("turnover_rate")
    private double turnoverRate;

    /**市盈率*/
    @Field("pe")
    private double pe;

    /**市净率*/
    @Field("pb")
    private double pb;

    /**流通市值*/
    @Field("cmv")
    private double circulationMarketValue;

    /**总市值*/
    @Field("total_value")
    private double totalValue;

    /**是否退市1：正常，9：已退市*/
    @Field("is_exist")
    private int isExist;

    /**更新时间*/
    @Field("update_time")
    private Date updateTime;

    /**写入时间*/
    @Field("create_time")
    private Date createTime;

    @Override
    public boolean equals(Object object){
        if(object instanceof BaseStockInfo){
            BaseStockInfo bs = (BaseStockInfo)object;
            return bs.getCode().equals(this.code);
        }
        return false;
    }
}
