package com.cqupt.mislab.erpn.dao.order;

import com.cqupt.mislab.erpn.model.entity.order.ChooseOrder;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @Author xhx
 * @Date 2021/2/1 21:35
 */
@Mapper
@Repository
public interface ChooseOrderDao {
    /**
     * 为一个学生增加一个订单
     * @param userUnique
     * @param period
     * @param marketName
     * @param productName
     * @param groupName
     * @param sequence 序列？
     */
    void addChooseOrder(@Param("userUnique") String userUnique, @Param("period")int period,@Param("marketName") String marketName,@Param("productName") String productName,@Param("groupName") String groupName,@Param("chooseValue") Integer sequence);

    /**
     * 更新Value
     * @param userUnique
     * @param period
     * @param chooseValue
     * @param marketName
     * @param productName
     */
    void updateChooseOrderValue(@Param("userUnique")String userUnique,@Param("period")Integer period,@Param("chooseValue") Integer chooseValue,@Param("marketName") String marketName,@Param("productName") String productName);

    /**
     * 找到已经选择的订单
     * @param userUnique
     * @return
     */
    List<ChooseOrder> findChooseOrderByUserUnique(String userUnique,int orderIncrement);

    /**
     * 更新value
     * @param orderId
     * @param chooseValue
     */
    void updateChooseOrderValueByOrderId(@Param("orderId") int orderId,@Param("isChosen") Integer chooseValue);


    /**
     * 更新所有的order
     * @param chooseValue
     * @param userUnique
     */
    void updateAllChooseOrderByUserUnique(@Param("chooseValue") Integer chooseValue,@Param("userUnique") String userUnique);

    int countChooseOrdersByGroupNameAndPeriod(@Param("groupName") String groupName, @Param("period") Integer period);

    int countChooseOrderByNaturalKey(@Param("userUnique") String userUnique,
                                     @Param("period") Integer period,
                                     @Param("marketName") String marketName,
                                     @Param("productName") String productName,
                                     @Param("groupName") String groupName);

    int deleteInvalidAdvertisementChooseOrders(@Param("period") Integer period,
                                               @Param("marketName") String marketName,
                                               @Param("productName") String productName,
                                               @Param("groupName") String groupName);

}
