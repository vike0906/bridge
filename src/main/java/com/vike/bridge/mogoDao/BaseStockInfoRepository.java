package com.vike.bridge.mogoDao;

import com.vike.bridge.mogoEntity.BaseStockInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: lsl
 * @createDate: 2019/9/23
 */
@Repository
public interface BaseStockInfoRepository extends MongoRepository<BaseStockInfo,String> {

}
