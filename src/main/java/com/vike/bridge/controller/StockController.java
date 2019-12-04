package com.vike.bridge.controller;

import com.vike.bridge.common.CommonResponse;
import com.vike.bridge.common.PageLimit;
import com.vike.bridge.mogoEntity.BaseStockInfo;
import com.vike.bridge.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: lsl
 * @createDate: 2019/12/2
 */
@Slf4j
@RestController
@RequestMapping("stock")
public class StockController {

    @Autowired
    StockService stockInfoService;

    @GetMapping("base")
    public CommonResponse<Page<BaseStockInfo>> base(@RequestParam(required = false)String exchange,
                       @RequestParam(required = false)String queryStr,
                       @RequestParam(value = "order",defaultValue = "change") String order,
                       @RequestParam(value = "direction",defaultValue = "descend")String direction,
                       PageLimit pageLimit){

        Page<BaseStockInfo> page = stockInfoService.selectBaseStockInfo(exchange, queryStr, order, direction, pageLimit);

        return new CommonResponse<>(page);
    }
}
