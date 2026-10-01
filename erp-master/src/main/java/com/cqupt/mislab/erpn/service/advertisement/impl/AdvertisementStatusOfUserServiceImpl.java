package com.cqupt.mislab.erpn.service.advertisement.impl;

import com.cqupt.mislab.erpn.commons.constant.AdvertiseAndOrderProgress;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementStatusOfUserDao;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.advertisement.Advertisement;
import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.vo.gamegroup.GameGroupMemberAdAndOrderInfoVo;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementStatusOfUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whl
 * @date 2021/3/26 - 20:18
 */
@Service
@Slf4j
public class AdvertisementStatusOfUserServiceImpl implements AdvertisementStatusOfUserService {

    @Autowired
    private AdvertisementStatusOfUserDao advertisementStatusOfUserDao;

    @Autowired
    private AdvertisementDao advertisementDao;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private AdvertisementServiceImpl advertisementServiceimpl;
    @Autowired
    private GroupManageDao groupManageDao;
    @Override
    public boolean findChooseOrderFlag(String userUnique, int currentPeriod) {
        Integer chooseOrderFlag = advertisementStatusOfUserDao.findChooseOrderFlag(userUnique, currentPeriod);
        return Integer.valueOf(1).equals(chooseOrderFlag);
    }

    @Override
    public boolean findFinishOrderFlag(String userUnique) {
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        Integer finishOrderFlag = advertisementStatusOfUserDao.findFinishOrderFlag(userUnique, currentPeriod);
        return Integer.valueOf(1).equals(finishOrderFlag);
    }

    @Override
    public Result findAdvertisementStatusOfUserByUserUnique(String userUnique) {
        if (!StringUtils.isEmpty(userUnique)) {
            int progress = AdvertiseAndOrderProgress.ALREADY_FINISH_ORDER.getProgress();
            Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
//            获取当前年
            Integer periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
            Integer currentYear = (currentPeriod - 1) / periodsOfOneYear + 1;
            int periodOfBegin = (currentYear - 1) * periodsOfOneYear + 1;
            AdvertisementStatusOfUser advertisementStatusOfUser = advertisementStatusOfUserDao.findAdvertisementStatusOfUserByUserUnique(userUnique, periodOfBegin);
//            查询已经投放广告的信息
            List<Advertisement> alreadyAdvertisement = advertisementDao.findAlreadyAdvertisement(userUnique);
            if (advertisementStatusOfUser != null) {
                if (advertisementStatusOfUser.getFinishAdvertiseFlag() == 1) {
//                    已经结束了广告投放
                    log.info("已经结束了投放广告");
                    if (advertisementStatusOfUser.getFinishOrderFlag() == 1){
                        progress = AdvertiseAndOrderProgress.ALREADY_FINISH_ORDER.getProgress();
                    }else if (advertisementStatusOfUser.getChooseOrderFlag() == 1) {
                        //已经可以开始选单了 就进入选订单的页面  4
                        log.info("可以开始选单了,进入选订单页面");
                        progress = AdvertiseAndOrderProgress.START_SELECT_ORDER.getProgress();
                    } else {
                        //校验是否出现了bug，所有成员都完成了选单，但是ChooseOrderFlag没有更新为1,所有检测一遍
                        Integer checkOtherMemberAdvertisementFinishFlag = advertisementServiceimpl.isCheckOtherMemberAdvertisementFinishFlag(userUnique, currentPeriod);
                        //进行校验
                        if (checkOtherMemberAdvertisementFinishFlag.equals(3)) {
                            progress=AdvertiseAndOrderProgress.START_SELECT_ORDER.getProgress();
                            log.info("小组成员已完成广告投放，即将进入选订单环节");
                        }else{
                            log.info("userUnique:{},已经完成了投放广告");
//                    向组内所有成员广播消息，某某某已经广告投放完毕并且推送type:6
                            progress=AdvertiseAndOrderProgress.WAIT_MEMBER_PUT.getProgress();
                        }

 //                       //还没有开始选单,就是进入等待界面   3
 //                       progress = AdvertiseAndOrderProgress.WAIT_MEMBER_PUT.getProgress();
                    }
                } else {
                    if (alreadyAdvertisement.size() != 0) {
//                        用户已经投放了广告
                        progress = AdvertiseAndOrderProgress.ALREADY_PUT_MONEY.getProgress();
                    }else {
//                        用户还未投放广告
                        progress = AdvertiseAndOrderProgress.UN_PUT_INTO_MONEY.getProgress();
                    }
                }
                return ResultUtil.success(progress);
            }
        }
        return ResultUtil.error();
    }

      //    TODO
    @Override
    public  synchronized Result  findMembersCurProgress(String userUnique) {
//        1.根据userUnique查询出还未破产的组员
        List<GameGroupMemberInfo> stillManage = groupManageDao.findGameGroupMemberListByStatus(1, userUnique);
        int maxYear = 1;
        for (GameGroupMemberInfo gameGroupMemberInfo : stillManage) {
            int currentPeriodOfCurMember = gameGroupMemberInfo.getCurrentPeriod();
            int periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(gameGroupMemberInfo.getUserUnique());
            int currentYear = (currentPeriodOfCurMember - 1) / periodsOfOneYear + 1;
            maxYear = currentYear > maxYear ? currentYear : maxYear;

        }
//        2.根据组员的userUnique查询组员的是否已经投放了广告金额
        List<GameGroupMemberAdAndOrderInfoVo> res = new ArrayList<>();
        for (GameGroupMemberInfo memberInfo : stillManage) {
            String memberUserUnique = memberInfo.getUserUnique();
//            2.1判断该组员是否已经注入了广告金额,根据查询用户投放的金额来判断用户是否已经投钱了
            Integer currentPeriod = commonDao.findCurrentPeriod(memberUserUnique);
//            获取当前年
            Integer periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(memberUserUnique);
            Integer currentYear = (currentPeriod - 1) / periodsOfOneYear + 1;
            int periodOfBegin = (currentYear - 1) * periodsOfOneYear + 1;
            Double summaryMoney = advertisementDao.findSummaryMoney(memberUserUnique, periodOfBegin);
            GameGroupMemberAdAndOrderInfoVo memberProgressInfo = null;
//            2.2若金额总数==0意味着该组员还没注入资金计算组员的年差，
//            若差值!=0则代表有一位组员比其它组员快一年，因此该组员还没投放广告
            Integer finishAdFlag = advertisementStatusOfUserDao.findFinishAdFlag(memberUserUnique, periodOfBegin);
            int yearDifference = maxYear - currentYear;
            if ((finishAdFlag==0&&summaryMoney == 0)||yearDifference!=0) {
                memberProgressInfo = getGameGroupMemberAdAndOrderInfoVo(memberUserUnique,AdvertiseAndOrderProgress.UN_PUT_INTO_MONEY);
            }else{
//               2.3 如果总金额大于0，那么再判断当前组员是否已经确认投放了广告

                Integer chooseOrderFlag = advertisementStatusOfUserDao.findChooseOrderFlag(memberUserUnique, periodOfBegin);
                Integer finishOrderFlag = advertisementStatusOfUserDao.findFinishOrderFlag(memberUserUnique, periodOfBegin);

//                2.4如果当前组员还未确认投放广告，那么该用户处于进度2
                if (finishAdFlag == 0) {
                     memberProgressInfo = getGameGroupMemberAdAndOrderInfoVo(memberUserUnique, AdvertiseAndOrderProgress.ALREADY_PUT_MONEY);
                }else if(finishAdFlag==1&&chooseOrderFlag==0){
//                   2.5 如果当前组员已经确认投放了广告且其它组员还未确认投放广告，那么当前组员的进度为3否则为4
                    memberProgressInfo = getGameGroupMemberAdAndOrderInfoVo(memberUserUnique, AdvertiseAndOrderProgress.WAIT_MEMBER_PUT);
                }else if(chooseOrderFlag==1&&finishOrderFlag==0) {
                    memberProgressInfo = getGameGroupMemberAdAndOrderInfoVo(memberUserUnique, AdvertiseAndOrderProgress.START_SELECT_ORDER);
                }
                else if(finishOrderFlag==1) {
                    memberProgressInfo = getGameGroupMemberAdAndOrderInfoVo(memberUserUnique, AdvertiseAndOrderProgress.ALREADY_FINISH_ORDER);
                }
            }
            res.add(memberProgressInfo);
        }
        log.info("组名为：{}，组员进度信息：{}", groupManageDao.findGroupNameByUserId(res.get(0).getUserId()).getGroupName(), res);

        return ResultUtil.success(res);

    }

    @Override
    public void initAdStatusOfUser(String userUnique) {
        int period = commonDao.findCurrentPeriod(userUnique);

        AdvertisementStatusOfUser advertisementStatusOfUser = new AdvertisementStatusOfUser();
        advertisementStatusOfUser.setUserUnique(userUnique);
        advertisementStatusOfUser.setPeriod(period);
        advertisementStatusOfUser.setFinishAdvertiseFlag(0);
        advertisementStatusOfUser.setFinishOrderFlag(0);
        advertisementStatusOfUser.setChooseOrderFlag(0);

        advertisementStatusOfUserDao.addAdStatusOfUser(advertisementStatusOfUser);

    }

    @Override
    public boolean checkFinishOrderFlag(String userUnique) {
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        Integer finishOrderFlag = advertisementStatusOfUserDao.findFinishOrderFlag(userUnique, currentPeriod);
        return Integer.valueOf(1).equals(finishOrderFlag);
    }

    private GameGroupMemberAdAndOrderInfoVo getGameGroupMemberAdAndOrderInfoVo(String memberUserUnique,AdvertiseAndOrderProgress andOrderProgress) {
        String memberUserId = commonDao.findUserIdByUserUnique(memberUserUnique);
        String gender = commonDao.findGenderByUserId(memberUserId);
        GameGroupMemberAdAndOrderInfoVo memberProgressInfo = GameGroupMemberAdAndOrderInfoVo.builder()
                .userId(memberUserId)
                .userName(commonDao.findUserNameByUserId(memberUserId))
                .userUnique(memberUserUnique)
                .gender(gender)
                .curProcess(andOrderProgress.getProgress())
                .message(andOrderProgress.getMessage())
                .build();
        return memberProgressInfo;
    }
}
