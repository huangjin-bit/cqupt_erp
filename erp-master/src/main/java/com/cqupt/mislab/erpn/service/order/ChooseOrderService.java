package com.cqupt.mislab.erpn.service.order;

import com.cqupt.mislab.erpn.model.vo.order.ChooseOrderVO;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/2/1 21:10
 */
public interface ChooseOrderService {
    /**
     *
     * updateChoosingOrder
     * 正在选择的订单的操作.
     * @param userUnique
     * @param orderId
     * @return
     *int
     * 根据返回不同的 数字来判断是是  返回的是什么状态
     * 1-成功选择							</br>
     * 2-还没有轮到你选的说.					</br>
     * 3-iso没有研发成功					</br>
     * 4-市场没有开发						</br>
     * 5-这张订单已经被取走了.					</br>
     * @exception
     * @since  1.0.0
     * @author hhy
     */
    int updateChoosingOrder(String userUnique, String orderId);

    /**
     * updateEndChooseOrder  结束所有的订货会
     * @param userUnique
     * @return
     *int
     * @exception
     * @since  1.0.0
     */
    int updateEndChooseOrder(String userUnique);

    /**
     * updateChooseOrderValue
     * 结束掉某个市场某个产品的 订单会 即退出这个订货会
     * @param orderId
     * @param chooseValue
     *void
     * @exception
     * @since  1.0.0
     */
    void updateChooseOrderValue(String orderId, Integer chooseValue);
    /**
     *findChooseOrderByMarketNameAndProductName 根据marketname 和 productname 来得到相应的订单信息.
     *  但是只是显示投过广告费用的那些市场.和投过广告的产品
     * @param chooseValue
     * @param userUnique
     */
    void updateChooseOrderValueByUserUnique(Integer chooseValue,String userUnique);
    /**
     * 拿到所有可以选择的订单，同时加入用户名属性，可以在选择订单时用用户名来显示（原来是userID）
     * @author 杨青
     * @param period
     * @param gameGroupName
     * @param marketName
     * @param productName
     * @param userUnique
     * @return
     * @since 2016/12/13
     */
    //老erp查询订单方法，物品的分类的逻辑在SQL中
    List<ChooseOrderVO> findChooseOrderByMarketNameAndProductName(String userUnique, String marketName, String productName);
    //目前erp的在前端中，返回所有结果,部分市场
    /**
     * 拿到所有可以选择的订单，同时加入用户名属性，可以在选择订单时用用户名来显示（原来是userID）
     * @author 杨青
     * @param period
     * @param gameGroupName
     * @param marketName
     * @param productName
     * @param userUnique
     * @return
     * @since 2016/12/13
     */
    List<ChooseOrderVO> findChooseOrderByMarketNameAndProductName(String userUnique);

//    List<String> getFirstOrder(String userUnique);

    /**
     * udpateIsClooseAll 检查是否已经选择完全部.如果是就直接结束掉
     * @param userUnique
     *void
     * @exception
     * @since  1.0.0
     */
    boolean updateIsCloseAll(String userUnique);

    boolean updateChooseOrderChooseValue(String userUnique,String marketName,String productName);
}
