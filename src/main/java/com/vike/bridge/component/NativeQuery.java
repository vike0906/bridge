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

        String sql = "SELECT  DATE(t.create_time) _date, COUNT(id) _count FROM b_sys_operate_log  t WHERE t.req_type=1 AND DATE(t.create_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE() GROUP BY _date";

        Query nativeQuery = entityManager.createNativeQuery(sql,RequestStatisticsVo.class);

        List<RequestStatisticsVo> requestList = (List<RequestStatisticsVo>) nativeQuery.getResultList();

        return requestList;
    }
}
