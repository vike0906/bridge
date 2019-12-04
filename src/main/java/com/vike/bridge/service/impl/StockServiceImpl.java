package com.vike.bridge.service.impl;

import com.vike.bridge.common.PageLimit;
import com.vike.bridge.mogoEntity.BaseStockInfo;
import com.vike.bridge.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/12/2
 */
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<BaseStockInfo> selectBaseStockInfo(String exchange, String queryStr, String order, String direction, PageLimit pageLimit) {
        Query query = new Query();

        if(!StringUtils.isEmpty(exchange)){
            Criteria criteria1 = Criteria.where("exchange").is(exchange);
            query.addCriteria(criteria1);
        }

        if(!StringUtils.isEmpty(queryStr)){
            String a = "^.*";
            String b = ".*$";
            queryStr = a+queryStr+b;
            Criteria criteria2 = Criteria.where("")
                    .orOperator(
                            Criteria.where("code").regex(queryStr),
                            Criteria.where("name").regex(queryStr));
            query.addCriteria(criteria2);
        }
        long count = mongoTemplate.count(query, BaseStockInfo.class);

        PageRequest pq = pageLimit.page("descend".equals(direction), order);

        query.with(pq);

        List<BaseStockInfo> baseStockInfos = mongoTemplate.find(query, BaseStockInfo.class);
        Page<BaseStockInfo> page = new PageImpl<>(baseStockInfos, pq, count);
        return page;
    }
}
