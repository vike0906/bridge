package com.vike.bridge.component;

import com.vike.bridge.vo.RequestStatisticsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author: lsl
 * @createDate: 2019/12/7
 */
@Slf4j
@Component
public class NativeQuery {

    @PersistenceContext
    EntityManager entityManager;

    /**系统七日内访问统计*/
    public List<RequestStatisticsVo> RequestStatistics(){

        String sql = "SELECT COUNT(id), DATE(t.create_time) _date FROM b_sys_operate_log  t WHERE DATE(t.create_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE() GROUP BY _date";

        Query nativeQuery = entityManager.createNativeQuery(sql, RequestStatisticsVo.class);

        List<RequestStatisticsVo> resultList = nativeQuery.getResultList();

        return resultList;
    }
}
