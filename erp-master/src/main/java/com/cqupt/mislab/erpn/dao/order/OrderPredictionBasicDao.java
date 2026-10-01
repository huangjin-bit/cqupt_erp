package com.cqupt.mislab.erpn.dao.order;

import com.cqupt.mislab.erpn.model.entity.prediction.OrderPredictionBasic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/12/22 11:25
 */

@Repository
@Mapper
public interface OrderPredictionBasicDao {
    List<OrderPredictionBasic> findAllOrderPredictionBasic();
}
