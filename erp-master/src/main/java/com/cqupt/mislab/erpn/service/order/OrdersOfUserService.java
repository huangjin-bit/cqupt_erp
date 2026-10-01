package com.cqupt.mislab.erpn.service.order;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.order.OrdersOfUser;
import com.cqupt.mislab.erpn.model.vo.order.OrdersOfUserVO;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author xhx
 * @Date 2021/2/3 9:25
 */
public interface OrdersOfUserService {
    /**
     * 通过不同的orderId获得锁
     * @param orderId
     * @return
     */
    public ReentrantLock getLock(String orderId);

    /**
     * 取出用户的所有订单
     * @author lx
     * @param userUnique
     * @return
     */
     List<OrdersOfUserVO> findOrdersOfUserByUserUnique(String userUnique);

    /**
     * 取出用户已交货的订单
     * @author lx
     * @param userUnique
     * @return
     */
     List<OrdersOfUser> findDeliveredOrdersOfUser(String userUnique);

    /**
     * 取出用户还没交货的订单
     * @param userUnique
     * @return
     */
     List<OrdersOfUser> findNotDeliverOrdersOfUser(String userUnique);

    /**
     * 取出最短交货订单的时间
     * @author lx
     * @return
     */
     Integer findSmallestTime(String userUnique);

    /**
     * 取出用户某个详细的订单
     * @author lx
     */
     OrdersOfUser findOrderDetail(String orderID);

    /**
     * 交货时的修改订单状态
     * @author lx
     */
     boolean updateOrderEndTime(OrdersOfUser ordersOfUser);

    /**
     * 取出订单销售金额
     * @author lx
     */
     float findSaleRoom(String ordersID);


    /**
     * 交货前的总检测，返回
     * 1：交货成功
     * 2：交货失败，请首先处理延误时间最长的订单
     * 3：交货失败，没有足够库存的商品
     * 4: 交货失败，未知错误
     * @param userUnique
     * @param orderId
     * @return
     */
    int updateDeliverCheck(String userUnique, String orderId);

    /**
     * 检测是否有未按时交货的订单
     *
     * @return
     * @author lx
     */
    boolean checkOrderOnTime(String userUnique);

    /**
     * 想要交货的订单是否正是拖延时间最长的未按时交货的订单  <br>
     * 1:表示该订单就是拖延时间最长的订单，即是最应该处理的订单！				  <br>
     * 0:表示该订单不是拖延最长的订单，请先处理最应该处理的订单！
     *
     * @param userUnique
     * @param orderId
     * @return
     * @author lx
     */
    int checkDelayLongest(String userUnique, String orderId);


    /**
     * 1：表示有足够的商品，可以交货				<br>
     * 0：表示没有足够的商品，不能交货			<br>
     * 检查库存中是否有足够的产品可供交货
     *
     * @param userUnique
     * @param orderId
     * @return
     */
    int checkEnough(String userUnique, String orderId);


    /**
     * 开始正式交货处理一系列操作	<br>
     * ：订单状态改变  			<br>
     * ：会计分录				<br>
     * ：产品库存减少			<br>
     * ------------------------------------<br>
     * param checkOnTime <br>
     * true 表示是未按期交货的订单<br>
     * false表示是按期交货的订单<br>
     *
     * @return
     * @author lx
     */
    boolean delieverHandle(String userUnique, String orderId, boolean checkOnTime);
}
