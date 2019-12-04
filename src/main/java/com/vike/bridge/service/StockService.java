package com.vike.bridge.service;

import com.vike.bridge.common.PageLimit;
import com.vike.bridge.mogoEntity.BaseStockInfo;
import org.springframework.data.domain.Page;

/**
 * @author: lsl
 * @createDate: 2019/9/24
 */
public interface StockService {

    Page<BaseStockInfo> selectBaseStockInfo(String exchange, String queryStr, String order, String direction, PageLimit pageLimit);

}
