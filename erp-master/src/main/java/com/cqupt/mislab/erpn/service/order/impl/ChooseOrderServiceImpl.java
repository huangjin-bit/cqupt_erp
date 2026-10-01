package com.cqupt.mislab.erpn.service.order.impl;

import com.alibaba.fastjson.JSONObject;
import com.cqupt.mislab.erpn.commons.utils.WsResultUtil;
import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.market.MarketDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementStatusOfUserDao;
import com.cqupt.mislab.erpn.dao.order.ChooseOrderDao;
import com.cqupt.mislab.erpn.dao.order.ChooseOrderVODao;
import com.cqupt.mislab.erpn.dao.order.OrdersOfUserDao;
import com.cqupt.mislab.erpn.dao.teacher.GameGroupInfoDao;
import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;
import com.cqupt.mislab.erpn.model.entity.order.AllOrdersOfGroup;
import com.cqupt.mislab.erpn.model.entity.order.ChooseOrder;
import com.cqupt.mislab.erpn.model.entity.order.OrdersOfUser;
import com.cqupt.mislab.erpn.model.vo.order.ChooseOrderVO;
import com.cqupt.mislab.erpn.model.statusChange.OrderSequenceStatusEnum;
import com.cqupt.mislab.erpn.model.vo.order.MarketProductPair;
import com.cqupt.mislab.erpn.service.iso.DevelopedISOService;
import com.cqupt.mislab.erpn.service.order.AllOrdersOfGroupService;
import com.cqupt.mislab.erpn.service.order.ChooseOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static com.cqupt.mislab.erpn.commons.constant.OrderConstant.ORDER_INCREMENT;

/**
 * @Author xhx
 * @Date 2021/2/1 21:11
 */
@Service
@Slf4j
public class ChooseOrderServiceImpl implements ChooseOrderService {
    @Resource
    private AllOrdersOfGroupService allordersOfGroupService;
    @Resource
    private MarketDao developedMarketDao;
    @Resource
    private ChooseOrderVODao chooseOrderVODao;
    @Resource
    private DevelopedISOService developedISOService;
    @Resource
    private ChooseOrderDao chooseOrderDao;
    @Resource
    private OrdersOfUserDao ordersOfUserDao;
    @Resource
    private CommonDao commonDao;
    @Resource
    private AdvertisementStatusOfUserDao advertisementStatusOfUserDao;
    @Resource
    private GameGroupInfoDao gameGroupInfoDao;
    @Resource
    private WebsocketEndPoint endPoint;
    //出现了两个人选择到同一个订单的情况。初步排查出现了并发问题
    private static final ConcurrentHashMap<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public int updateChoosingOrder(String userUnique, String orderId) {
        int orderStatus = 5;
        // 获取对应 orderId 的锁对象，如果不存在则创建一个新的
        ReentrantLock lock = lockMap.computeIfAbsent(orderId, k -> new ReentrantLock());
        //先做基本判断，枷锁后再判断
        if(allordersOfGroupService.findOrderStatusByOrderId(orderId)!=0||ordersOfUserDao.findOrderDetail(orderId)!=null){
            return OrderSequenceStatusEnum.ORDER_STATUS_EXCEPTION.getStatusCode();
        }
        try {
            // 加锁
            lock.lock();
            AllOrdersOfGroup orders = allordersOfGroupService.findAllOrdersOfGroupByOrderId(orderId);

            //加锁后判断
            if(allordersOfGroupService.findOrderStatusByOrderId(orderId)==0&&ordersOfUserDao.findOrderDetail(orderId)==null){

                //还没有被获取的订单
                if(developedMarketDao.findDevelopedMarket(userUnique,orders.getMarketName()) == 1)//developedMarketDao.isDevelopedMarket(userUnique, orders.getMarketName())
                {
                    //这里本来要判断这个产品是否已经开发完成..但是后面要改成能够提前选择没有开发完成的产品所以就不做这里的判断 .
                    if(developedISOService.isDevelopedISO(userUnique, orders.getSpecialRem())){
                        //判断这张单是否轮到这个user选择
                        if(isUserUniqueCanChoosing(userUnique,orders)){
                            //chooseOrderDao.refreshChooseOrderCache();
                            //创建用户与订单的联系
                            OrdersOfUser orderOfUser = new OrdersOfUser(userUnique,orders.getOrderID(),orders.getProductName(),orders.getPNumber(),orders.getPrice(),orders.getMarketName(),orders.getNeedTime(),orders.getMoneyTime(),orders.getPenalPercent(),orders.getSpecialRem(),null);
                            //更新订单状态
                            allordersOfGroupService.updateOrderStatusByOrderId(orderId,1);
                            //持久化用户与订单联系
                            ordersOfUserDao.addOrderOfUser(orderOfUser);
                            //选单是全局轮转，成功选中一单后当前用户在所有市场/产品上都后移一轮
                            chooseOrderDao.updateAllChooseOrderByUserUnique(commonDao.findGroupMemberSize(userUnique), userUnique);
                            String message = JSONObject.toJSONString(WsResultUtil.orderChosenResponse("订单选择完成！"));
                            endPoint.publish(commonDao.findGameGroupNameByUserUnique(userUnique), new TextMessage(message));
                            orderStatus = OrderSequenceStatusEnum.USER_CHOOSING.getStatusCode();
                        }else{
                            //没有轮到这个用户选择,不能选择这张单.
                            orderStatus = OrderSequenceStatusEnum.WAITING_CHOOSE.getStatusCode();
                        }
                    }else{
                        //iso验证没有研发成功,选单失败.
                        orderStatus = OrderSequenceStatusEnum.ISO_VERIFY_FAILED.getStatusCode();
                    }
                }else{
                    //市场没有开发完成,不能选择这个市场的订单
                    orderStatus = OrderSequenceStatusEnum.MARKET_VERIFY_FAILED.getStatusCode();
                }
            }else{
                //如果不等于0 就是 已经被人拿走了.这张订单已经不能被选的说
                orderStatus = OrderSequenceStatusEnum.ORDER_STATUS_EXCEPTION.getStatusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            //出现异常
            orderStatus = OrderSequenceStatusEnum.ORDER_STATUS_EXCEPTION.getStatusCode();
        } finally {
            // 解锁
            lock.unlock();
        }

        return orderStatus;
    }

    /* @Override
    public int updateChoosingOrder(String userUnique, String orderId) {
        int orderStatus = 5;
        AllOrdersOfGroup orders = allordersOfGroupService.findAllOrdersOfGroupByOrderId(orderId);

        if(allordersOfGroupService.findOrderStatusByOrderId(orderId)==0){
            //还没有被获取的订单
            if(developedMarketDao.findDevelopedMarket(userUnique,orders.getMarketName()) == 1)//developedMarketDao.isDevelopedMarket(userUnique, orders.getMarketName())
            {
                //这里本来要判断这个产品是否已经开发完成..但是后面要改成能够提前选择没有开发完成的产品所以就不做这里的判断 .
                if(developedISOService.isDevelopedISO(userUnique, orders.getSpecialRem())){
                    //判断这张单是否轮到这个user选择
                    if(isUserUniqueCanChoosing(userUnique,orders)){
                        //chooseOrderDao.refreshChooseOrderCache();
                        //创建用户与订单的联系
                        OrdersOfUser orderOfUser = new OrdersOfUser(userUnique,orders.getOrderID(),orders.getProductName(),orders.getPNumber(),orders.getPrice(),orders.getMarketName(),orders.getNeedTime(),orders.getMoneyTime(),orders.getPenalPercent(),orders.getSpecialRem(),null);
                        //更新订单状态
                        allordersOfGroupService.updateOrderStatusByOrderId(orderId,1);
                        //持久化用户与订单联系
                        ordersOfUserDao.addOrderOfUser(orderOfUser);
                        //更新当前用户顺序信息
                        chooseOrderDao.updateChooseOrderValue(userUnique,orders.getPeriod(), commonDao.findGroupMemberSize(userUnique),orderOfUser.getMarketName(),orderOfUser.getProductName());
                        String message = JSONObject.toJSONString(WsResultUtil.orderChosenResponse("订单选择完成！"));
                        endPoint.publish(commonDao.findGameGroupNameByUserUnique(userUnique), new TextMessage(message));
                        orderStatus = OrderSequenceStatusEnum.USER_CHOOSING.getStatusCode();
                    }else{
                        //没有轮到这个用户选择,不能选择这张单.
                        orderStatus = OrderSequenceStatusEnum.WAITING_CHOOSE.getStatusCode();
                    }
                }else{
                    //iso验证没有研发成功,选单失败.
                    orderStatus = OrderSequenceStatusEnum.ISO_VERIFY_FAILED.getStatusCode();
                }
            }else{
                //市场没有开发完成,不能选择这个市场的订单
                orderStatus = OrderSequenceStatusEnum.MARKET_VERIFY_FAILED.getStatusCode();
            }
        }else{
            //如果不等于0 就是 已经被人拿走了.这张订单已经不能被选的说
            orderStatus = OrderSequenceStatusEnum.ORDER_STATUS_EXCEPTION.getStatusCode();
        }
        return orderStatus;
    }*/

    @Override
    public int updateEndChooseOrder(String userUnique) {
        //数据库力chooseValue的增量
        Integer period = gameGroupInfoDao.findCurrentTime(userUnique);
        AdvertisementStatusOfUser adStatusOfUser = advertisementStatusOfUserDao.findAdvertisementStatusOfUserByUserUnique(userUnique, period);

        if(adStatusOfUser.getFinishAdvertiseFlag()==1){
            //已经投放完广告.
            if(adStatusOfUser.getFinishOrderFlag()==0){
                advertisementStatusOfUserDao.updateFinishOrderFlag(userUnique, period, 1);
                updateChooseOrderValueByUserUnique(ORDER_INCREMENT,userUnique);
                return 1;
            }else{
                //已经完成这一期的选择订单的操作了.
                return 2;
            }
        }else{
            //如果不等于 1 就是没有完成投放广告
            return 3;
        }
    }

    @Override
    public void updateChooseOrderValue(String orderId, Integer chooseValue) {
        //强转操作
        try {
            chooseOrderDao.updateChooseOrderValueByOrderId(Integer.parseInt(orderId), chooseValue);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            log.info("orderId{},出现问题！----ChooseOderServiceImp.updateChooseOrderValue",orderId);
        }
    }

    @Override
    public void updateChooseOrderValueByUserUnique(Integer chooseValue, String userUnique) {
        chooseOrderDao.updateAllChooseOrderByUserUnique(chooseValue,userUnique);
    }

    @Override
    public List<ChooseOrderVO> findChooseOrderByMarketNameAndProductName(String userUnique, String marketName, String productName) {
        String gameGroupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        int period = gameGroupInfoDao.findCurrentTime(userUnique);
        log.info(period+"------------------"+"period");
        if("".equals(marketName) || marketName==null){
            marketName="all";
        }
        if("".equals(productName) || productName==null){
            productName="all";
        }

        List<ChooseOrderVO> chooseOrder = chooseOrderVODao.findChooseOrderValueObject(period, gameGroupName, marketName, productName, userUnique);
        for (ChooseOrderVO chooseOrderVO : chooseOrder){
            String userName = commonDao.findUserNameByUserUnique(chooseOrderVO.getUserUnique());
            log.info(userName+"----------------"+"user");
            chooseOrderVO.setUserName(userName);
        }
        return chooseOrder;
    }

    @Override
    public List<ChooseOrderVO> findChooseOrderByMarketNameAndProductName(String userUnique){
        String gameGroupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        int period = gameGroupInfoDao.findCurrentTime(userUnique);
        log.info(period+"------------------"+"period");

        List<ChooseOrderVO> chooseOrder = chooseOrderVODao.findChooseOrderValueObject(period, gameGroupName, "all", "all", userUnique);
        HashSet<MarketProductPair> MarketProductPairHashSet=chooseOrderVODao.getMarketProductPairs(period,userUnique,ORDER_INCREMENT,gameGroupName);
        List<ChooseOrderVO> chooseOrderVos=new ArrayList<>();

        for (ChooseOrderVO chooseOrderVO : chooseOrder){
            String userName = commonDao.findUserNameByUserUnique(chooseOrderVO.getUserUnique());
            log.info(userName+"----------------"+"user");
            chooseOrderVO.setUserName(userName);
        }
        //清洗已经结束了的市场产品
        if(!MarketProductPairHashSet.isEmpty()){
            for(ChooseOrderVO chooseOrderVO : chooseOrder){
                MarketProductPair mp=new MarketProductPair(chooseOrderVO.getMarketName(),chooseOrderVO.getProductName());
                if(!MarketProductPairHashSet.contains(mp)){
                    chooseOrderVos.add(chooseOrderVO);
                }
            }
            return chooseOrderVos;
        }
        return chooseOrder;
    }
//    @Override
//    public List<String> getFirstOrder(String userUnique){
//        List<String> ans = new ArrayList<>(4);
//        String gameGroupName = commonDao.findGameGroupNameByUserUnique(userUnique);
//        int period = gameGroupInfoDao.findCurrentTime(userUnique);
//        List<String> usersOrder = chooseOrderVODao.findUsersOrder(period, gameGroupName);
//        for(int i = 1; i < usersOrder.size(); i++){
//               ans.add("p"+i+usersOrder.get(i - 1));
//        }
//        return ans;
//    }

    @Override
    public boolean updateIsCloseAll(String userUnique) {
        List<ChooseOrder> chooseOrderList = chooseOrderDao.findChooseOrderByUserUnique(userUnique,ORDER_INCREMENT);
        if(chooseOrderList.isEmpty()){
            this.updateEndChooseOrder(userUnique);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateChooseOrderChooseValue(String userUnique,String marketName,String productName) {
        int period = gameGroupInfoDao.findCurrentTime(userUnique);
        chooseOrderDao.updateChooseOrderValue(userUnique,period,ORDER_INCREMENT,marketName,productName);
        return false;
    }

    private boolean isUserUniqueCanChoosing(String userUnique, AllOrdersOfGroup orders){
        String chooserUser = chooseOrderVODao.findChooserOrderUser(orders.getMarketName(), orders.getProductName(), orders.getPeriod(), orders.getGroupName());
        log.info("当前应该选择的人是{}",chooserUser);
        if(userUnique!=null && chooserUser!=null){
            if(userUnique.equals(chooserUser)) return true;
        }
        return false;
    }

}
