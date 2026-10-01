package com.cqupt.mislab.erpn.service.forwardquarter.impl;

import com.alibaba.fastjson.JSON;
import com.cqupt.mislab.erpn.commons.constant.ForwardStatusEnum;
import com.cqupt.mislab.erpn.commons.constant.WsResultEnum;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementStatusOfUserDao;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.balance.BalanceSheetDao;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.WebsocketResult;
import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.vo.finance.BalanceSheetVo;
import com.cqupt.mislab.erpn.model.vo.gamegroup.ForwardQuaterVo;
import com.cqupt.mislab.erpn.model.vo.gamegroup.GameGroupVo;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementStatusOfUserService;
import com.cqupt.mislab.erpn.service.forwardquarter.ForwardQuaterService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 张烈文
 * @date 2021/9/8 13:25
 */
@Service
@Slf4j
public class ForwardQuaterServiceImpl implements ForwardQuaterService {


    @Autowired
    private WebsocketEndPoint websocketEndPoint;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private GroupManageService groupManageService;

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private AdvertisementStatusOfUserService advertisementStatusOfUserService;
    @Autowired
    private AdvertisementStatusOfUserDao advertisementStatusOfUserDao;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private BalanceSheetDao balanceSheetDao;
    @Autowired
    private CashDao cashDao;
    @Autowired
    private GroupManageDao groupManageDao;
    private final static double bankruptcyFactor = 1.5;
    @Override
    public synchronized Result forwardStatus(String userUnique) {
//       返回给前端，前端根据此状态码判断是否需要推进
//                0：用户已经破产
//                1：用户正在经营
//                2：年初但未完成广告费投放和订单选取确认
//                3：游戏已经结束
//        当前用户比赛的状态
        int status = groupMemberService.findStatusByUserUnique(userUnique);
        log.info("当前用户比赛状态：{}", status);
        ForwardQuaterVo forwardQuaterVo = new ForwardQuaterVo();

        try {
//           若用户已经破产
            if (status == ForwardStatusEnum.BANKRUPTCY.getStatus()) {
                log.info("用户已破产");
                forwardQuaterVo.setForwardStatus(ForwardStatusEnum.BANKRUPTCY.getStatus());
                forwardQuaterVo.setMessage(ForwardStatusEnum.BANKRUPTCY.getMessage());

                return ResultUtil.success(forwardQuaterVo);
            } else {
//               检查是否是年初
                boolean yearBegin = groupManageService.isYearBegin(userUnique);
//                检查用户是否完成了“广告投放”和“选订单”
                boolean finishOrderFlag = advertisementStatusOfUserService.checkFinishOrderFlag(userUnique);
                if (yearBegin && !finishOrderFlag) {
                    //表示是年初并且用户“未”完成广告投放和选订单
                    forwardQuaterVo.setForwardStatus(ForwardStatusEnum.ADVERTISEMENT_AND_ORDER_NOT_YET.getStatus());
                    forwardQuaterVo.setMessage(ForwardStatusEnum.ADVERTISEMENT_AND_ORDER_NOT_YET.getMessage());
                    return ResultUtil.success(forwardQuaterVo);
                } else {
//                    oracle下一周期的存储函数调用
                    Integer periodBefore = commonDao.findCurrentPeriod(userUnique);
                    commonDao.runFunctionOracle(userUnique, 0);

//                    检查用户是否破产
                    log.info("Forward推进下一周期，userUnique:{}", userUnique);
                    boolean isGameOver = isGameOver(userUnique);
                    log.info("isGameOver:{}", isGameOver);
                    if (isGameOver) {
//                        破产清算,并返回
                        bankruptcy(userUnique);
                        forwardQuaterVo.setForwardStatus(ForwardStatusEnum.BANKRUPTCY.getStatus());
                        forwardQuaterVo.setMessage(ForwardStatusEnum.BANKRUPTCY.getMessage());
                        return ResultUtil.success(forwardQuaterVo);
                    }
                    //第16步 检查用户是否已经完成比赛
                    //注意：我们直接处理需要更新的操作，如果不需要更新，则不会对数据库进行操作的.
                    boolean isEndGame = updateFinishedGame(userUnique);
                    if (isEndGame) {
                        forwardQuaterVo.setForwardStatus(ForwardStatusEnum.GAME_OVER.getStatus());
                        forwardQuaterVo.setMessage(ForwardStatusEnum.GAME_OVER.getMessage());
                        return ResultUtil.success(forwardQuaterVo);
                    }
                    //绝对设置下一周期，不受 biz_next_deal 篡改影响
                    int expectedPeriod = (periodBefore != null ? periodBefore : 0) + 1;
                    groupManageDao.updateSetUserCurrentPeriod(userUnique, expectedPeriod);

                    //第15步 是否更新GAMEGROUP表中的CurrentPeriod字段值。
                    String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
                    updateGroupCurrentPeriod(groupName);
                    boolean yearBegin1 = groupManageService.isYearBegin(userUnique);
                    log.info("是否年初：{}", yearBegin1);
                    //bug：年末无法推进下一周期；我感觉就是这里的问题了,年末推进就要考虑广告选单问题：广告投放，这里为一年的最后一期，导致无法推进
                    if (yearBegin1) {
                        advertisementService.initAdOfUser(userUnique);
                        advertisementStatusOfUserService.initAdStatusOfUser(userUnique);
                    }
                    forwardQuaterVo.setForwardStatus(ForwardStatusEnum.RECOMMEND_A_CYCLE.getStatus());
                    forwardQuaterVo.setMessage(ForwardStatusEnum.RECOMMEND_A_CYCLE.getMessage());
                    return ResultUtil.success(forwardQuaterVo);
                }
            }
        } catch (Exception e) {
            log.error("推进下一周期报错啦！！:{}",e.getMessage());
            return ResultUtil.error();
        }
    }

    private void updateGroupCurrentPeriod(String groupName) {
        int minPeriod = groupManageDao.findLeastCurrentPeriodByGroupName(groupName);
        int currentPeriod = groupManageDao.findCurrentPeriodByGroupName(groupName);
        if (currentPeriod < minPeriod) {
            groupManageDao.updateGroupCurrentPeriod(groupName, minPeriod);
        }

    }

    private boolean updateFinishedGame(String userUnique) {
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        GameGroupVo gameGroupVO = groupManageDao.findGameGroupVo(userUnique,groupName);
        int CurrentPeriod = gameGroupVO.getCurrentPeriod();
        int getTotalPeriodOfGroup = gameGroupVO.getTotalPeriodOfGroup();

        if (CurrentPeriod == getTotalPeriodOfGroup) {
            groupManageDao.updateUserStatusToFinishGame(userUnique);
            System.out.println("CurrentPeriod" + CurrentPeriod);
            System.out.println("getTotalPeriodOfGroup" + getTotalPeriodOfGroup);
            return true;
        }

        return false;

    }

    private void bankruptcy(String userUnique) {
//        1.更新status-设置用户状态未0（破产）
        groupManageDao.updateBankruptcyUserStatus(userUnique, 0);
        GameGroupInfo gameGroupInfo = new GameGroupInfo();
        gameGroupInfo = groupManageDao.findGameGroupYearAndCurrentPeriod(userUnique);
        //理论上是不需要这样的. 但是还是要将这个人这期给结束掉. 如果第一期就退出比赛的话.
        advertisementStatusOfUserDao.updateFinishAdFlag(userUnique, 1, 1);
        advertisementStatusOfUserDao.updateFinishOrderFlag(userUnique, 1, 1);
        int A = gameGroupInfo.getCurrentPeriod();        //当前期数
        int B = gameGroupInfo.getPeriodsOfOneYear();    //每年的期数
        int C = gameGroupInfo.getYears();                //年数
        int D = 1;                                        //计算出的每年开始的第一期
        log.info("ForwardQuater-->bankruptcy:A:{},B:{},C:{},D:{}", A, B, C, D);
//        计算出每一年第一周期，然后向数据库中插入每年第一周期的投广告和选订单记录，防止其它用户无法推进下一周期的情况
//        如果不添加记录，其它用户将永远卡在等待大厅，不能进行下一步操作
        while (true) {
            if (D > A && D <= (C - 1) * B + 1) {
                insertDataToAdststusofuser(userUnique, D);
            }
            if (D > (C - 1) * B + 1) {
                break;
            } else {
                D = D + B;
                //继续执行if语句
            }
        }
        /**
         * @author 杨青周军
         * @Since 2017/03/08
         */
        //判断是否需要生成订单
        advertisementService.checkGenerateOrderIfNecessary(userUnique);
//        TODO 计算出当前小组的人数，然后广播通知其他用户，若当前用户破产，而其它用户都已经完成了广告投放
//        websocket返回type7，倘若其它用户还有未投放广告的，那么依然在等待大厅，当前破产用户websocket返回type9
        WebsocketResult websocketResult = new WebsocketResult();
        websocketResult.setType(WsResultEnum.ADVERTISE_COMPLETE.getType());
        websocketResult.setMessage(WsResultEnum.ADVERTISE_COMPLETE.getMessage());
        String text = JSON.toJSONString(websocketResult);
        log.info("test:{}", text);
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        websocketEndPoint.publish(groupName, new TextMessage(text));
//        TODO 以上可能还有问题，待测
    }

    private void insertDataToAdststusofuser(String userUnique, int D) {
        AdvertisementStatusOfUser advertisementStatusOfUser = new AdvertisementStatusOfUser();
        advertisementStatusOfUser.setUserUnique(userUnique);
        advertisementStatusOfUser.setPeriod(D);
        advertisementStatusOfUser.setFinishAdvertiseFlag(1);
        advertisementStatusOfUser.setFinishOrderFlag(1);
        advertisementStatusOfUser.setChooseOrderFlag(1);

        advertisementStatusOfUserDao.addAdStatusOfUser(advertisementStatusOfUser);
    }

    private boolean isGameOver(String userUnique) {
        if (isBalanceSheetOut(userUnique) | !isHaveCash(userUnique)) {
            return true;
        } else {
            return false;
        }

    }

    private boolean isHaveCash(String userUnique) {
        double cash = cashDao.findCash(userUnique);
        log.info("现金:{}", cash);
        if (cash < 0) {
            return false;
        } else {
            return true;
        }

    }

    private boolean isBalanceSheetOut(String userUnique) {

        log.info("Forward->isBalanceSheetOut:{}", userUnique);
        List<BalanceSheetVo> balancesheetVOs = balanceSheetDao.findBalanceSheetVo(userUnique);
        float assets = 0, liability = 0;
        for (int i = 0; i < balancesheetVOs.size(); i++) {
            BalanceSheetVo balancesheetVO = balancesheetVOs.get(i);

            if ("资产".equals(balancesheetVO.getType1())) {
                assets += balancesheetVO.getCvalue();
            }

            if ("负债".equals(balancesheetVO.getType1())) {
                liability += balancesheetVO.getCvalue();
            }
        }


        if ((bankruptcyFactor * assets) < liability) {
            return true;
        }
        return false;
    }
}
