package com.cqupt.mislab.erpn.service.advertisement.impl;


import com.cqupt.mislab.erpn.commons.constant.AdvertisementResultEnum;
import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.market.MarketDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementStatusOfUserDao;
import com.cqupt.mislab.erpn.dao.order.ChooseOrderDao;
import com.cqupt.mislab.erpn.dao.order.OrdersOfUserDao;
import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.entity.advertisement.Advertisement;
import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;
import com.cqupt.mislab.erpn.model.entity.market.DevelopedMarket;
import com.cqupt.mislab.erpn.model.entity.order.OrderSequence;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static com.cqupt.mislab.erpn.commons.constant.OrderConstant.ORDER_INCREMENT;

/**
 * @author 张烈文
 */
@Service
@Slf4j
public  class AdvertisementServiceImpl implements AdvertisementService {

    @Autowired
    private MarketDao marketDao;

    @Autowired
    private AdvertisementDao advertisementDao;

    @Autowired
    private CommonDao commonDao;

    @Autowired
    private AdvertisementStatusOfUserDao advertisementStatusOfUserDao;

    @Autowired
    private CashDao cashDao;

    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private AccountDetailService accountDetailService;

    @Autowired
    private GroupManageDao groupManageDao;

    @Autowired
    private WebsocketEndPoint websocketEndPoint;

    @Autowired
    private OrdersOfUserDao ordersOfUserDao;
    @Autowired
    private ChooseOrderDao chooseOrderDao;
    @Autowired
    private ProductBasicDao productBasicDao;

    @Autowired
    private AdAndOrderRefreshService adAndOrderRefreshService;

    @Override
    public Result findDevelopedMarketByUserUnique(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("userUnique={}", userUnique);
            return ResultUtil.error();
        }
        List<DevelopedMarket> developedMarkets = marketDao.getMarketsByUserUnique(userUnique);

        return ResultUtil.success(developedMarkets);
    }

    @Override
    public Result findAdvertisementByMarketNameAndUserUnique(String marketName, HttpSession session) {
//        1.判断userUnique是否为空
        String userUnique = (String) session.getAttribute("userUnique");
        if (StringUtils.isEmpty(userUnique)) {
            log.error("UserUnique={},marketName={}", userUnique, marketName);
            return ResultUtil.error();
        }
        if (StringUtils.isEmpty(marketName)) {
            marketName = "本地市场";
        }
//        2.获取已开拓市场的所有广告
        List<Advertisement> advertisements = advertisementDao.findAdvertisementByMarketNameAndUserUnique(marketName, userUnique);
        return ResultUtil.success(advertisements);
    }

    @Override
    public Result updateAdvertisementForProduct(Integer adId, Double money) {
//        1.判断adId是否为空
        if (StringUtils.isEmpty(adId)) {
            log.error("adId={}", adId);
            return ResultUtil.error();
        }
//        2.修改某个广告所投入的money
        Boolean byAdId = advertisementDao.updateMoneyByAdId(adId, money);
        if (byAdId) {
            return ResultUtil.success();
        }
        return ResultUtil.error();
    }

    @Override
    public Result findAlreadyAdvertisement(String userUnique) {
        List<Advertisement> adList = advertisementDao.findAlreadyAdvertisement(userUnique);
        return ResultUtil.success(adList);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public Result updateAdvertisementFinish(String userUnique) {
//        1.获取当前周期
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
//        2.查询用户是否已经投过广告
        /*
         * 2021.9.19 修改只能每年初可以结束经营的bug  张烈文&王瀚林
         */
        Integer periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        Integer curYear = (currentPeriod - 1) / periodsOfOneYear + 1;
        //todo:推进下一周期时会执行这条语句，由于数据库中没有这条记录，所以会查出来null，进而在下面的判断中抛出空指针异常；异常抛到stopOperation方法中，会被捕捉，导致无法在年初之外的时间结束经营
        Integer adStatusPeriod = (curYear - 1) * periodsOfOneYear + 1;
        Integer finishAdFlag = advertisementStatusOfUserDao.findFinishAdFlag(userUnique, adStatusPeriod);

        if (AdvertisementResultEnum.FINISH_ADVERTISEMENT_EXIST.getStatus().equals(finishAdFlag)) {
            log.warn("你已经投放过广告");
            return ResultUtil.error(AdvertisementResultEnum.FINISH_ADVERTISEMENT_EXIST.getStatus(),AdvertisementResultEnum.FINISH_ADVERTISEMENT_EXIST.getMessage());
        }else {
            log.info("开始投放广告");
//         3.查找用户在当前周期投放的所有广告费用
            Double summaryMoneyOfAd = advertisementDao.findSummaryMoney(userUnique, currentPeriod);
//         4.获得当前可用的现金
            Double cash = cashDao.findCash(userUnique);

            if (cash < summaryMoneyOfAd && cash > 0) {
                log.info("投入的钱={},当前现金={}", summaryMoneyOfAd, cash);
                //2015.10.07 如果现金少于0，已经不要命了。那就无所谓了。随意点就ok 了。
                //如果现钱太少的话就要直接退出.如果不是的话就直接继续投放.
                //"结束广告费投入操作不成功！当前投入的广告费超出了企业所拥有的现金，请降低广告费的投放或者贷款获得更多的现金！";
                return ResultUtil.error(
                          AdvertisementResultEnum.FINISH_ADVERTISEMENT_FAIL.getStatus()
                        , AdvertisementResultEnum.FINISH_ADVERTISEMENT_FAIL.getMessage());
            }else{
             // 产生相应会计分录
                accountHeadService.addAccountHead(userUnique,currentPeriod,"投广告费");
                accountDetailService.addAccountDetail(userUnique, "营业费用", "现金", "借", "贷", summaryMoneyOfAd, summaryMoneyOfAd, (-summaryMoneyOfAd));
//                2015.1.16 王永何海源 联合对比调试
//                减少现金
                 cashDao.reduceCash(userUnique, summaryMoneyOfAd);

//               更新表adstatusofuser的字段finishadflag设置为 1  先将自己的状态量变成了 1

                 advertisementStatusOfUserDao.updateFinishAdFlag(userUnique, adStatusPeriod, 1);

//               检查组内其它成员的投广告的情况，同时，如果组内其他人已经投放完广告，则产生订单顺序

                Integer result = isCheckOtherMemberAdvertisementFinishFlag(userUnique, adStatusPeriod);


                if (result.equals(3)) {
//                   当组内所有人广告都投放完毕后,是否可以直接返回一个状态码给前端，
//                   前端收到消息的方法中通过我返回的状态码来控制是向用户推送消息还是让组内所有的用户跳转到选订单页面
                    adAndOrderRefreshService.allMembersComplete(userUnique);
                    log.info("小组成员已完成广告投放，即将进入选订单环节");

                }else{
                    log.info("userUnique:{},已经完成了投放广告");
//                    向组内所有成员广播消息，某某某已经广告投放完毕并且推送type:6
                    adAndOrderRefreshService.advertiseComplete(userUnique);
                }
                log.info("投放广告完毕");
                return ResultUtil.success();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean checkGenerateOrderIfNecessary(String userUnique) {
        //获取还没有破产的成员(status=1)
        final Integer userStatus = 1;

        List<GameGroupMemberInfo> gameGroupList = groupManageDao.findGameGroupMemberListByStatus(userStatus, userUnique);
        if (gameGroupList.size() == 0) {
            return false;
        }
        int period = gameGroupList.get(0).getCurrentPeriod();// 获取第一个没破产用户的当前期数
        //比较所有未破产用户是否在同一个时期
        for (int i = 1; i < gameGroupList.size(); i++) {
            if (period != gameGroupList.get(i).getCurrentPeriod()) {
                return false;
            }
        }
        String[] userUniques = new String[gameGroupList.size()];

        for (int i = 0; i < gameGroupList.size(); i++) {
            GameGroupMemberInfo gameGroupMember = gameGroupList.get(i);
            userUniques[i] = gameGroupMember.getUserUnique();
        }
        //判断所有未破产用户是否投完广告费并处在等待大厅
        List<AdvertisementStatusOfUser> advertisementStatusOfUsers = advertisementStatusOfUserDao.findAdvertisementStatusOfUserByUserUniques(userUniques, period);
        if (gameGroupList.size() != advertisementStatusOfUsers.size()) {
            return false;
        }
        GameGroupInfo gameGroupInfo = groupManageDao.findGameGroupInfoByUserUnique(userUnique);
        boolean allAlreadyInChooseOrder = true;
        for (AdvertisementStatusOfUser advertisementStatusOfUser : advertisementStatusOfUsers) {
            if (advertisementStatusOfUser.getFinishAdvertiseFlag() == 0) {
                return false;
            }
            if (advertisementStatusOfUser.getChooseOrderFlag() != 1) {
                allAlreadyInChooseOrder = false;
            }
        }

        boolean chooseOrdersChanged = generateChooseOrders(gameGroupList, gameGroupInfo, period, true);
        if (!chooseOrdersChanged && allAlreadyInChooseOrder) {
            return false;
        }
        advertisementStatusOfUserDao.updateChooseOrderFlag(userUniques, period, 1);

        updateFinishedMemberChooseOrders(gameGroupList, period);

        return true;
    }

    @Override
    public void initAdOfUser(String userUnique) {
        List<Advertisement> list = advertisementDao.findMarketNameAndProductName();
        int period = commonDao.findCurrentPeriod(userUnique);

        for (int i = 0; i < list.size(); i++) {
            Advertisement ad = list.get(i);
            ad.setPeriod(period);
            ad.setUserUnique(userUnique);
            ad.setMoney((double) 0);
        }
        System.out.println(list.size());
	/*	Map<String, Object> map = new HashMap<String, Object>();
		map.put("list", MPs);*/
        advertisementDao.addAdvertisementList(list);
    }

    /**
     *
     * @param userUnique
     * @param period
     * @return  1---组内成员有人未完成广告投放，
     *          2---组内有人比其他人快一年,广告投放完毕,只能在等待大厅等待
     *
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public Integer isCheckOtherMemberAdvertisementFinishFlag(String userUnique, Integer period) {

        //获取还没有破产的成员  这里的1 代表是  用户的status
        // 用户的status: 1,代表正常经营的状态；2018-12-24 by 王永、刘岽
        final Integer userStatus = 1;
        List<GameGroupMemberInfo> gameGroupList = groupManageDao.findGameGroupMemberListByStatus(userStatus, userUnique);

        String[] userUniques = new String[gameGroupList.size()];

        for (int i = 0; i < gameGroupList.size(); i++) {
            GameGroupMemberInfo gameGroupMember = gameGroupList.get(i);
            userUniques[i] = gameGroupMember.getUserUnique();
        }

        List<AdvertisementStatusOfUser> advertisementStatusOfUsers = advertisementStatusOfUserDao.findAdvertisementStatusOfUserByUserUniques(userUniques, period);

        if (gameGroupList.size() == advertisementStatusOfUsers.size()) {
            for (AdvertisementStatusOfUser advertisementStatusOfUser : advertisementStatusOfUsers) {
                if (advertisementStatusOfUser.getFinishAdvertiseFlag() == 0) {
                    //因为其他成员没有完成投放广告的操作所以不能开始选单操作.
                    log.info("UserUnique={}开始等待其它人投广告");
                    return 1;
                }
            }
        } else {
            //可能有些大伙已经超级快的开始一年的广告投放了,但是又一些人还是没有的说
            //"成功结束广告费投放，但由于组内其它成员未达到你当前年份，所以你还不能进入选单操作";
            log.info("UserUnique={}快一年了丫，等着其他人", userUnique);
            return 2;
        }

        GameGroupInfo gameGroupInfo = groupManageDao.findGameGroupInfoByUserUnique(userUnique);
        generateChooseOrders(gameGroupList, gameGroupInfo, period, false);

        //更新所有的用户变成选单状态. 这里更新了状态之后应该要推送给所用用户准备跳转页面的说.!
        advertisementStatusOfUserDao.updateChooseOrderFlag(userUniques, period, 1);

        updateFinishedMemberChooseOrders(gameGroupList, period);
        return 3;
    }

    private boolean generateChooseOrders(List<GameGroupMemberInfo> gameGroupList, GameGroupInfo gameGroupInfo, Integer period, boolean queryByUser) {
        boolean changed = false;
        List<String> marketNames = marketDao.findMarketName();
        List<String> productNames = productBasicDao.findAllProductName();
        String groupName = gameGroupInfo.getGroupName();
        Integer end = gameGroupInfo.getCurrentPeriod() - 1;
        Integer begin = gameGroupInfo.getCurrentPeriod() - gameGroupInfo.getPeriodsOfOneYear();

        for (String marketName : marketNames) {
            for (String productName : productNames) {
                List<OrderSequence> orderSequencesA = queryByUser
                        ? ordersOfUserDao.findOrdersMoneyOfUser(marketName, productName, begin, end, groupName)
                        : ordersOfUserDao.findOrdersMoney(marketName, productName, begin, end, groupName);
                List<OrderSequence> orderSequencesB = queryByUser
                        ? ordersOfUserDao.findUnOrdersMoneyOfUser(marketName, productName, begin, end, groupName)
                        : ordersOfUserDao.findUnOrdersMoney(marketName, productName, begin, end, groupName);
                List<OrderSequence> moneySequence = countABMoney(gameGroupList, orderSequencesA, orderSequencesB);

                this.sortedListByMoneyValueDESC(moneySequence);
                if (period == 1) {
                    this.countListValue(moneySequence, 0);
                } else {
                    this.countListValue(moneySequence, 1);
                }

                chooseOrderDao.deleteInvalidAdvertisementChooseOrders(period, marketName, productName, groupName);
                List<OrderSequence> adSequence = advertisementDao.findAdvertisementMoneyOfUsers(groupName, period, marketName, productName);
                if (adSequence.isEmpty()) {
                    continue;
                }
                this.sortedListByMoneyValueDESC(adSequence);
                this.countListValue(adSequence, 2);

                List<OrderSequence> orderSequence = this.countAverageValue(adSequence, moneySequence);
                this.sortedListByMoneyValueASC(orderSequence);
                this.sortedOrderList(orderSequence);
                changed = this.saveChooseOrders(orderSequence, groupName, marketName, productName, period) || changed;
            }
        }
        return changed;
    }

    private void updateFinishedMemberChooseOrders(List<GameGroupMemberInfo> gameGroupList, Integer period) {
        for (GameGroupMemberInfo gameGroupMember : gameGroupList) {
            AdvertisementStatusOfUser status = advertisementStatusOfUserDao.findAdvertisementStatusOfUserByUserUnique(gameGroupMember.getUserUnique(), period);
            if (status != null && Integer.valueOf(1).equals(status.getFinishOrderFlag())) {
                chooseOrderDao.updateAllChooseOrderByUserUnique(ORDER_INCREMENT, gameGroupMember.getUserUnique());
            }
        }
    }

    private boolean saveChooseOrders(List<OrderSequence> orderSequence, String groupName, String marketName, String productName, Integer period) {
        boolean changed = false;
        for (int i = 0; i < orderSequence.size(); i++) {
            OrderSequence order = orderSequence.get(i);
            int existingRows = chooseOrderDao.countChooseOrderByNaturalKey(order.getUserUnique(), period, marketName, productName, groupName);
            if (existingRows == 0) {
                chooseOrderDao.addChooseOrder(order.getUserUnique(), period, marketName, productName, groupName, order.getSequence());
                changed = true;
            }
        }
        return changed;
    }

    private void sortedOrderList(List<OrderSequence> orderSequence) {
        for (int i = 0; i < orderSequence.size(); i++) {
            orderSequence.get(i).setSequence(i + 1);
        }
    }

    private void sortedListByMoneyValueASC(List<OrderSequence> orderSequence) {
        for (int i = 0; i < orderSequence.size() - 1; i++) {
            for (int j = 0; j < orderSequence.size() - 1 - i; j++) {
                OrderSequence order1 = orderSequence.get(j);
                OrderSequence order2 = orderSequence.get(j + 1);
                if (order1.getMoney() > order2.getMoney()) {
                    OrderSequence temp = order1;
                    orderSequence.set(j, order2);
                    orderSequence.set(j + 1, temp);
                }
            }
        }
    }

    private List<OrderSequence> countAverageValue(List<OrderSequence> adSequence, List<OrderSequence> moneySequence) {
        List<OrderSequence> list = new ArrayList<OrderSequence>();

        for (int i = 0; i < adSequence.size(); i++) {
            String userUnique = adSequence.get(i).getUserUnique();
            OrderSequence order = new OrderSequence();
            order.setUserUnique(userUnique);
            order.setMoney(adSequence.get(i).getMoney());

            for (int j = 0; j < moneySequence.size(); j++) {
                if (userUnique.equals(moneySequence.get(j).getUserUnique())) {
                    order.setMoney((order.getMoney() + moneySequence.get(j).getMoney()) / 2);
                    break;
                }
            }
            list.add(order);
        }
        return list;

    }

    private void countListValue(List<OrderSequence> moneySequence, int score) {
        for (int i = 0; i < moneySequence.size(); i++) {
            moneySequence.get(i).setMoney(score * (i + 1) + 0.0);
        }
    }

    private void sortedListByMoneyValueDESC(List<OrderSequence> moneySequence) {

        for (int i = 0; i < moneySequence.size() - 1; i++) {
            for (int j = 0; j < moneySequence.size() - 1 - i; j++) {
                OrderSequence order1 = moneySequence.get(j);
                OrderSequence order2 = moneySequence.get(j + 1);
                if (order1.getMoney() < order2.getMoney()) {
                    OrderSequence temp = order1;
                    moneySequence.set(j, order2);
                    moneySequence.set(j + 1, temp);
                }
            }
        }

    }

    private List<OrderSequence> countABMoney(List<GameGroupMemberInfo> gameGroupList, List<OrderSequence> orderSequencesA, List<OrderSequence> orderSequencesB) {

        //System.out.println("进入计算所有用户的A-B值的 列表长度：" + );
        //循环 members 出来 然后 相应的用户相减 money值. 最后得到一个orderSequence
        List<OrderSequence> list = new ArrayList<OrderSequence>();
        for (int i = 0; i < gameGroupList.size(); i++) {
            OrderSequence order = new OrderSequence();
            order.setUserUnique(gameGroupList.get(i).getUserUnique());
            order.setMoney(0.0);
            for (int j = 0; j < orderSequencesA.size(); j++) {
                if (gameGroupList.get(i).getUserUnique().equals(orderSequencesA.get(j).getUserUnique())) {
                    order.setMoney(orderSequencesA.get(j).getMoney());
                }
            }

            for (int j = 0; j < orderSequencesB.size(); j++) {
                if (gameGroupList.get(i).getUserUnique().equals(orderSequencesB.get(j).getUserUnique())) {
                    order.setMoney(order.getMoney() - orderSequencesB.get(j).getMoney());
                }
            }
            list.add(order);
        }
        return list;
    }

}
