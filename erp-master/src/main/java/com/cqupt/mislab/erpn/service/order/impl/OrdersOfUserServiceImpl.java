package com.cqupt.mislab.erpn.service.order.impl;

import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.finance.tax.TaxSheetDao;
import com.cqupt.mislab.erpn.dao.finance.will.WillReceiveDao;
import com.cqupt.mislab.erpn.dao.order.OrdersOfUserDao;
import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import com.cqupt.mislab.erpn.model.entity.finance.will.WillReceive;
import com.cqupt.mislab.erpn.model.entity.order.OrdersOfUser;
import com.cqupt.mislab.erpn.model.entity.product.ProductOfUser;
import com.cqupt.mislab.erpn.model.vo.order.OrdersOfUserVO;
import com.cqupt.mislab.erpn.service.finance.other.PenaltyCashService;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import com.cqupt.mislab.erpn.service.order.OrdersOfUserService;
import com.cqupt.mislab.erpn.service.product.ProductBasicService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author xhx
 * @Date 2021/2/3 9:26
 */
@Service
public class OrdersOfUserServiceImpl implements OrdersOfUserService {
    @Autowired
    private OrdersOfUserDao ordersOfUserDao;
    @Autowired
    private OrdersOfUserService ordersOfUserService;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private ProductBasicDao productBasicDao;
    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private AccountDetailService accountDetailService;
    @Autowired
    private CashDao cashDao;
    @Autowired
    private WillReceiveDao willReceiveDao;
    @Autowired
    private TaxSheetDao taxSheetDao;
    @Autowired
    private ProductBasicService productBasicService;
    @Autowired
    private PenaltyCashService penaltyCashService;

    //存放不同orderId的锁
    ConcurrentHashMap<String,ReentrantLock> locks= new ConcurrentHashMap<>();


    //根据orderId获得锁，如果存在直接返回，如果不存在就创建一个存储在locks里面
    @Override
    public  ReentrantLock  getLock(String orderId) {
        ReentrantLock lock=locks.get(orderId);

        if(lock==null){
            lock= new ReentrantLock();
            locks.put(orderId,lock);
        }

        return lock;
    }

    @Override
    public List<OrdersOfUserVO> findOrdersOfUserByUserUnique(String userUnique) {
        List<OrdersOfUser> ordersOfUsers = ordersOfUserDao.findOrdersOfUserByUserUnique(userUnique);
        List<OrdersOfUserVO> ordersOfUserVOS = new ArrayList<>();
        for (OrdersOfUser order:ordersOfUsers) {
            OrdersOfUserVO ordersOfUserVO = new OrdersOfUserVO();
            BeanUtils.copyProperties(order, ordersOfUserVO);
            if (StringUtils.isEmpty(order.getEndTime())) {
                ordersOfUserVO.setStatus(0);
            }else{
                ordersOfUserVO.setStatus(1);
            }
            ordersOfUserVOS.add(ordersOfUserVO);
        }
        return ordersOfUserVOS;
    }

    @Override
    public List<OrdersOfUser> findDeliveredOrdersOfUser(String userUnique) {
        return ordersOfUserDao.findDeliveredOrdersOfUserByUserUnique(userUnique);
    }

    @Override
    public List<OrdersOfUser> findNotDeliverOrdersOfUser(String userUnique) {
        return ordersOfUserDao.findNotDeliverOrdersOfUser(userUnique);
    }

    @Override
    public Integer findSmallestTime(String userUnique) {
        return ordersOfUserDao.findSmallestTime(userUnique);
    }

    @Override
    public OrdersOfUser findOrderDetail(String orderID) {
        return ordersOfUserDao.findOrderDetail(orderID);
    }

    @Override
    public boolean updateOrderEndTime(OrdersOfUser ordersOfUser) {
        return ordersOfUserDao.updateOrderEndTime(ordersOfUser);
    }

    @Override
    public float findSaleRoom(String ordersID) {
        return ordersOfUserDao.findSaleRoom(ordersID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDeliverCheck(String userUnique, String orderId) {

        //根据orderId的不同分配锁
        ReentrantLock lock=getLock(orderId);
        //自旋方式加锁
        while(true) {
            if(lock.tryLock()) break;
        }

        HashMap<String, Integer> res = new HashMap<>();
        try {
            //System.out.println("交货的订单为："+orderId);
            boolean C = this.checkOrderOnTime(userUnique);
            //System.out.println(C);
            //防止重复提交订单
            if (ordersOfUserDao.findOrderDetail(orderId)==null||ordersOfUserDao.findOrderDetail(orderId).getEndTime()!=null){
                return 4;
            }
            if (C) {
                //表示有未按期交货的订单，则转步骤2检查想要交货的订单是否为拖延时间最长的未按时交货的订单
                int checkDelayLongest = this.checkDelayLongest(userUnique, orderId);
                if (checkDelayLongest == 0) {
                    //如果该订单不是延误时间最长的订单，提示“请首先处理延误时间最长的订单！”，维持用户界面为当前界面，处理结束
                    //System.out.println("请首先处理延误时间最长的订单");
                    return 2;
                } else {
                    //当前处理的订单即为拖延时间最长的未交货订单，转步骤3检查库存中是否有足够的产品可供交货
                    int checkEnough = this.checkEnough(userUnique, orderId);
                    if (checkEnough == 0) {
                        //如果库存不够交货，则提示“库存中没有足够的产品可供交货！”，维持用户界面为当前界面，处理结束。
                        //System.out.println("库存中没有足够的产品可供交货！");

                        return 3;
                    } else {
                        //如果库存足够交货，则转入步骤四，完成交货工作
                        boolean deliver = this.delieverHandle(userUnique, orderId, C);
                        if (deliver) {
                            //填写会计分录等数据库操作
                            //System.out.println("会计分录填写成功");
                        } else {
                            //System.out.println("会计分录填写失败，交货失败");
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return 0;
                        }
                    }
                }
            } else {

                //表示没有未按期交货的订单,则转步骤3检查库存中是否有足够的产品可供交货
                int checkEnough = this.checkEnough(userUnique, orderId);
                //System.out.println(checkEnough);
                if (checkEnough == 0) {
                    //如果库存不够交货，则提示“库存中没有足够的产品可供交货！”，维持用户界面为当前界面，处理结束。
                    //System.out.println("库存中没有足够的产品可供交货！");
                    return 3;
                } else {
                    //如果库存足够交货，则转入步骤四，完成交货工作
                    boolean deliver = this.delieverHandle(userUnique, orderId, C);
                    if (deliver) {
                        //填写会计分录等数据库操作
                        //System.out.println("会计分录填写成功");
                    } else {
                        //System.out.println("会计分录填写失败，交货失败");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                        return 0;
                    }
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return 4;
        } finally {
            lock.unlock();
        }
        return 1;
    }

    @Override
    public boolean checkOrderOnTime(String userUnique) {
        //用于表示是否有未按期交货的订单，C为True表示有未按期交货的订单，C为False表示没有未按期交货的订单
        boolean C = false;
        //取出最早交货但是没有还没交货的订单
        Integer SmallestTime = ordersOfUserService.findSmallestTime(userUnique);
        //从游戏用户表中
        int GCPeriod = commonDao.findCurrentPeriod(userUnique);

        if (SmallestTime!=null&&SmallestTime < GCPeriod) {
            //如果最小值比CurrentPeriod小，表示没有未按期交货的订单
            C = true;
        }
        return C;
    }

    @Override
    public int checkDelayLongest(String userUnique, String orderId) {
        int SmallestTime = ordersOfUserService.findSmallestTime(userUnique);
        int NeedTime = ordersOfUserService.findOrderDetail(orderId).getNeedTime();
        if (SmallestTime == NeedTime) {
            //交货日期最小值与订单的交货日期相等，表示当前处理的订单即为拖延时间最长的未交货订单
            return 1;
        } else {
            //交货日期最小值与订单的交货日期不相等，则应首先处理延误时间最长的订单
            return 0;
        }
    }

    @Override
    public int checkEnough(String userUnique, String orderId) {
        String productName;
        int pNumber = 0;            //用户的商品的库存量
        int pNumberOfOrder = 0;        //订单上商品的需求量
        OrdersOfUser order = ordersOfUserService.findOrderDetail(orderId);
        pNumberOfOrder = order.getPNumber();
        productName = order.getProductName();

        ProductOfUser productOfUser = new ProductOfUser();
        productOfUser.setUserUnique(userUnique);
        productOfUser.setProductName(productName);

        pNumber = productBasicDao.findPNumberByUserUniqueAndProductName(productOfUser);
        if (pNumber >= pNumberOfOrder) {
            //如果用户拥有的产品的数量大于或等于应交货的交货数量，则可以交货
            return 1;
        } else {
            //如果用户拥有否认产品的数量小于应交货的交货数量，则返回0
            return 0;
        }
    }

    @Override
    public boolean delieverHandle(String userUnique, String orderId, boolean checkOnTime) {

        String productName = ordersOfUserService.findOrderDetail(orderId).getProductName();
        //System.out.println("产品名为："+productName);
        //填写订单交货日期
        OrdersOfUser ordersOfUser = ordersOfUserService.findOrderDetail(orderId);
        ordersOfUserService.updateOrderEndTime(ordersOfUser);

        float A = ordersOfUserService.findSaleRoom(orderId);//订单产生的卖价
        //System.out.println("出售商品的收入A为："+A);
        int happenTime = commonDao.findCurrentPeriod(userUnique);//当前时期

        //插入第一条会计分录信息（出售商品收入） 。
        accountHeadService.addAccountHead(userUnique, happenTime, "出售产品");
        accountDetailService.addAccountDetail(userUnique, "应收账款", "主营业务收入", "借", "贷", (double) A, (double) A, (double) A);

        //在应收账款表WILLRECEIVE中增加一条记录
        int endTime = ordersOfUser.getMoneyTime();
        WillReceive willReceive = WillReceive.builder()
                .userUnique(userUnique)
                .money((double) A)
                .beginTime(happenTime)
                .endTime(happenTime + endTime)
                .willReceiveDescription("按订单交货产生的应收账款")
                .note("").build();

        willReceiveDao.insertWillReceive(willReceive);
        //lx:四舍五入处理float数
        //插入第二条会计分录信息(缴纳的税金)。
        Double TT = ERPUtils.round((A * taxSheetDao.findTaxRate()));
        System.out.println("税率为:" + taxSheetDao.findTaxRate() + "   商品的卖价:" + A + "  缴纳的税金TT为：" + TT);
        System.out.println(taxSheetDao.findTaxRate());
        System.out.println(A);
        System.out.println(TT);
        accountHeadService.addAccountHead(userUnique, happenTime, "销售产品的营业税");
        accountDetailService.addAccountDetail(userUnique, "主营业务税金", "应交税金", "借", "贷", (double) TT, (double) TT, (double) TT);

        //增加税金表中的数值
        taxSheetDao.updateTaxSheet(userUnique, TT);

        //插入第三条会计分录(主营业务成本相关)。
        //产品成本
        float B = productBasicService.findProductPrice(productName);

        //2015.1.16 何海源王永对比测试.
        //获取订单中产品的数量,然后  原料的    单价*数量 得到    原料成本金额
        OrdersOfUser ordersOfUser1 = ordersOfUserService.findOrderDetail(orderId);
        //lx:四舍五入处理
        B = ERPUtils.round(B * ordersOfUser1.getPNumber());


        accountHeadService.addAccountHead(userUnique, happenTime, "出售产品的成本核算");
        accountDetailService.addAccountDetail(userUnique, "主营业务成本", "产品", "借", "贷", (double) B, (double) B, (double) (-B));

        //减少商品的库存数量
        int orderPNumber = ordersOfUserService.findOrderDetail(orderId).getPNumber();//订单上需要的产品数量
        int updatedRows = productBasicDao.updateDecreasePNumber(userUnique, orderPNumber, productName);
        if (updatedRows != 1) {
            throw new IllegalStateException("产品库存不足，交货失败");
        }

        //插入第四条会计分录(其他业务支出)。在未按时交货，违约的情况下才执行
        if (checkOnTime) {
            double D = penaltyCashService.computePenaltyCash(orderId);//未按时交货的违约金---还没方法提供，等待中！
            accountHeadService.addAccountHead(userUnique, happenTime, "订单未按时交货的违约金");
            accountDetailService.addAccountDetail(userUnique, "其他业务支出", "现金", "借", "贷", D, D, (-D));
            //将现金表CASHSHEET中的Cash字段值减少D
            cashDao.reduceCash(userUnique, D);
        }

        return true;
    }
}
