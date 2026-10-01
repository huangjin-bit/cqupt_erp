package com.cqupt.mislab.erpn.dao.order;

import com.cqupt.mislab.erpn.model.entity.order.OrderSequence;
import com.cqupt.mislab.erpn.model.entity.order.OrdersOfUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/2/2 18:39
 */
@Repository
@Mapper
public interface OrdersOfUserDao {
    /**
     * 增加一个用户的订单
     * @param ordersOfUser
     * @return
     */
    Boolean addOrderOfUser(OrdersOfUser ordersOfUser);
    /**
     * 通过UserUnique查找订单
     * @param userUnique
     * @return
     */
    List<OrdersOfUser> findOrdersOfUserByUserUnique(String userUnique);

    /**
     * 查找被选择的订单
     * @param userUnique
     * @return
     */
    List<OrdersOfUser> findDeliveredOrdersOfUserByUserUnique(String userUnique);

    /**
     * 查找没被选择的订单
     * @param userUnique
     * @return
     */
    List<OrdersOfUser> findNotDeliverOrdersOfUser(String userUnique);

    /**
     *找到最小时间？
     * @param userUnique
     * @return
     */
    Integer findSmallestTime(String userUnique);

    /**
     * 查看订单细节
     * @param orderID
     * @return
     */
    OrdersOfUser findOrderDetail(String orderID);

    /**
     * 更新endtime？
     * @param ordersOfUser
     * @return
     */
    boolean updateOrderEndTime(OrdersOfUser ordersOfUser);

    /**
     * 查询销售房间？？
     * @param ordersID
     * @return
     */
    float findSaleRoom(String ordersID);

    /**
     * 获取订单的钱？
     * @param marketName
     * @param productName
     * @param begin
     * @param end
     * @param groupName
     * @return
     */
    List<OrderSequence> findOrdersMoney(@Param("marketName") String marketName, @Param("productName") String productName, @Param("begin") Integer begin, @Param("end") Integer end, @Param("groupName") String groupName);

    /**
     * 获取没有被选择订单的钱
     * @param marketName
     * @param productName
     * @param begin
     * @param end
     * @param groupName
     * @return
     */
    List<OrderSequence> findUnOrdersMoney(@Param("marketName") String marketName, @Param("productName") String productName, @Param("begin") Integer begin, @Param("end") Integer end, @Param("groupName") String groupName);

    /**
     * findOrdersMoneyOfUser
     * 计算所有未破产用户在上一年中在该市场内对该产品进行   按时  交货的订单金额总和(设为A)
     *
     * @param marketName
     * @param productName
     * @param begin
     * @param end
     * @param groupName
     * @return List<OrderSequence>
     * @throws
     * @author hhy
     * @since 1.0.0
     */
    List<OrderSequence> findOrdersMoneyOfUser(@Param("marketName") String marketName,@Param("productName") String productName,@Param("begin") Integer begin,@Param("end") Integer end,@Param("groupName") String groupName);

    /**
     * findUnOrdersMoneyOfUser
     * 计算所有未破产用户在上一年中在该市场内对该产品进行  未按时  交货的订单金额总和（设为B）
     *
     * @param marketName
     * @param productName
     * @param begin
     * @param end
     * @param groupName
     * @return List<OrderSequence>
     * @throws
     * @author hhy
     * @since 1.0.0
     */
    List<OrderSequence> findUnOrdersMoneyOfUser(@Param("marketName") String marketName, @Param("productName") String productName, @Param("begin") Integer begin, @Param("end") Integer end, @Param("groupName") String groupName);


}
