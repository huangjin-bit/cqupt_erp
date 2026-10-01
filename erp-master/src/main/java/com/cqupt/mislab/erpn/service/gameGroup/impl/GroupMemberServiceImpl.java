package com.cqupt.mislab.erpn.service.gameGroup.impl;

import com.alibaba.fastjson.JSONObject;
import com.cqupt.mislab.erpn.commons.constant.GroupManageConstant;
import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.user.LoginUserDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupMemberService;
import com.cqupt.mislab.erpn.service.user.UserService;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementStatusOfUserService;
import com.cqupt.mislab.erpn.service.order.ChooseOrderService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.utils.WsResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: com.cqupt.mislab.erpn.service.impl
 * @description:
 * @author: keji0
 * @date: 2020/8/16 2:12
 */
@Service
@Slf4j
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    AdvertisementStatusOfUserService advertisementStatusOfUserService;

    @Autowired
    AdvertisementDao advertisementDao;

    @Autowired
    GroupMemberService groupMemberService;

    @Autowired
    UserService userService;

    @Autowired
    LoginUserDao loginUserDao;

    @Autowired
    GroupManageDao groupManageDao;

    @Autowired
    GroupManageService groupManageService;

    @Autowired
    CommonDao commonDao;

    @Autowired
    WebsocketEndPoint websocketEndPoint;

    @Autowired
    AdvertisementService advertisementService;

    @Autowired
    ChooseOrderService chooseOrderService;

    @Autowired
    AdAndOrderRefreshService adAndOrderRefreshService;



//    @Autowired @Qualifier("commonWebsocketService") private CommonWebsocketServicePublisher webSocketMessagePublisher;

    /**
     * 指定用户加入指定分组，若加入成功则向组员广播此人加入成功的消息
     * @param userId
     * @param groupName
     * @param session
     * @return
     */
    @Override
    public Result joinGroup(String userId, String groupName, HttpSession session) {
        try {
            //非空判断
            if (groupName != null && userId != null && !"".equals(userId)) {
                //组创建者不可能加入一个组
                if (!userService.isGroupMember(userId)) {
                    final GameGroupInfo groupInfo = groupManageDao.findGroupByGroupName(groupName);

                    /**
                     * 获取当前组的人数，判断人数是否超出限额
                     * 若没超出则把用户加入分组
                     */
                    int memberNum = loginUserDao.findGroupUserMessageByGroupName(groupName).size();
                    if (memberNum < groupInfo.getUserNumbers() ) {
                        Boolean added = addJoinInGroup(userId, groupName);
                        if(!added){
                            return ResultUtil.error("加入分组失败");
                        }
                        session.setAttribute("groupName", groupName);
                        List<GameGroupMemberInfo> groupMemberInfos = loginUserDao.findGroupMembersByGroupName(groupName);
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("groupMessage",groupInfo);
                        map.put("groupMemberList",groupMemberInfos);
                        //更新userInfo状态
                        UserPo userPo = (UserPo)session.getAttribute("userInfo");
                        userPo.setStatus(8);
                        session.setAttribute("userInfo", userPo);
                        //向组内成员广播加入者id、加入成功等信息
                        String message = JSONObject.toJSONString(WsResultUtil.UpdateMember(userId + GroupManageConstant.GROUP_JOIN_SUCCESS + groupName));
                        log.info(message);
//                        log.info(userId + GroupManageConstant.GROUP_JOIN_SUCCESS + groupName);
                        websocketEndPoint.publish(groupName,new TextMessage(message));
                        return ResultUtil.success(map);
                    }else {
                        return ResultUtil.error("人数已满");
                    }
                }else {
                    return ResultUtil.error(ResultEnum.ERROR);
                }
            }else {
                return ResultUtil.error(ResultEnum.ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error();
        }
    }

    /**
     * 单独写的加入分组操作数据库的方法，业务逻辑在joinGroup方法中补充
     * @param userId
     * @param groupName
     * @return
     */
    @Override
    public Boolean addJoinInGroup(String userId, String groupName) {
        try {
            groupManageDao.addGroupMember(groupName,userId);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 用户退出分组，并向组内成员广播此人退出的消息
     * @param userId
     * @param groupName
     * @param session
     * @return
     */
    @Override
    public Result quitGroup(String userId,String groupName, HttpSession session) {
        try {
            if (groupName != null && userId != null && !"".equals(userId) && !"".equals(groupName)) {
                if (groupName.equals(commonDao.findGameGroupNameByUserId(userId))) {
                    memberQuitGroup(userId, groupName);
                    session.removeAttribute("groupName");
                    //向组员广播此人退出分组的消息
                    String message = JSONObject.toJSONString(WsResultUtil.UpdateMember(userId + GroupManageConstant.GROUP_QUIT_SUCCESS + groupName));
                    log.info("{}",message);
                    websocketEndPoint.publish(groupName,new TextMessage(message));
                    //更新userInfo状态
                    UserPo userPo = (UserPo)session.getAttribute("userInfo");
                    userPo.setStatus(3);
                    session.setAttribute("userInfo", userPo);
                    return ResultUtil.success(ResultEnum.SUCCESS);
                } else {
                    return ResultUtil.error(userId + "不是" + groupName + "组成员!");
                }
            } else {
                return ResultUtil.error(ResultEnum.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.error(ResultEnum.ERROR);
        }
    }

    /**
     * 单独写的退出分组操作数据库的方法，业务逻辑在quitGroup方法中补充
     * @param userId
     * @param groupName
     * @return
     */
    @Override
    public Boolean memberQuitGroup(String userId, String groupName) {
        try {
            groupManageDao.deleteGroupMember(groupName,userId);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean updateUserUnqiqueAndCurrentPeriod(String groupName) {
        try {
            List<GameGroupMemberInfo> gameGroupMembers = loginUserDao.findGroupMembersByGroupName(groupName);
            //取出每条记录，用userID+时间联合创建userUnique，并且设置CurrentPeriod

            for(int i=0; i<gameGroupMembers.size(); i++) {
                GameGroupMemberInfo gameGroupMemberInfo = gameGroupMembers.get(i);
                gameGroupMemberInfo.setCurrentPeriod(1);
                String userID = gameGroupMemberInfo.getUserId();
                String userUnique = userID + ERPUtils.getStringDate();
                gameGroupMemberInfo.setUserUnique(userUnique);
                gameGroupMemberInfo.setStatus(1);
                gameGroupMemberInfo.setGroupName(groupName);
                loginUserDao.updateGameGroupMember(gameGroupMemberInfo);
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 房主踢人
     * 首先验证是否是房间创建者，然后更新数据库成员信息，再单独推送到该组所有成员
     * @param influencedId 受影响的人
     * @param groupName
     * @param session
     * @return
     */
    @Override
    public Boolean kickMember(String masterId,String influencedId, String groupName, HttpSession session) {
        try {
            //验证用户是否是房主
            if(userService.isGameCreator(masterId)&&userService.isGroupMember(masterId)){
                Boolean res = groupManageDao.deleteGroupMember(groupName, influencedId);
                //广播消息
                if(res) {
                    String message = JSONObject.toJSONString(WsResultUtil.kickMember(influencedId + "已被" + masterId + GroupManageConstant.KICKED_OFF_SUCCESS,influencedId));
                    log.info(message);
                    websocketEndPoint.publish(groupName, new TextMessage(message));
                }
            }
        }catch (NullPointerException e){
            log.info("踢出操作数据字段为空！");
        }catch (Exception e){
            log.info("提出操作未知异常");
        }
        websocketEndPoint.publish(groupName,new TextMessage(JSONObject.toJSONString("非法操作!")));
        return false;
    }

    @Override
    public Result stopOperation(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        try {
            if (!StringUtils.isEmpty(userUnique)){
                //清算广告单
                advertisementService.updateAdvertisementFinish(userUnique);
                //判断用户是否破产，未破产才能进行后续操作
                int status = loginUserDao.getStatusByUserUnique(userUnique);
                if (status != 0){
                    //判断当前是否处于年初
                    if (groupManageService.isYearBegin(userUnique)){
                        int currentPeriod = commonDao.findCurrentPeriod(userUnique);
                        double summaryMoney = advertisementDao.findSummaryMoney(userUnique, currentPeriod);
                        //投了广告费还没开始选订单，这种情况是不允许退出的
                        if (summaryMoney != 0 && !advertisementStatusOfUserService.findChooseOrderFlag(userUnique, currentPeriod)){
                            log.error("该用户投广告费总额不为0且还未开始选订单");
                            return ResultUtil.error("该用户投广告费总额不为0且还未开始选订单");
                        }

                        //清算广告单
                        advertisementService.updateAdvertisementFinish(userUnique);

                        if (!advertisementStatusOfUserService.findFinishOrderFlag(userUnique)){
                            //结束该用户的选订单流程
                            chooseOrderService.updateEndChooseOrder(userUnique);
                            //通知别人选订单
                            adAndOrderRefreshService.advertiseComplete(userUnique);
                        }
                    }

                    //修改用户状态为破产状态
                    groupManageDao.updateBankruptcyUserStatus(userUnique, 0);
                }

                //本来用户在session中的status存的是7或8（在游戏中）
                //这里把session中UserPo的status属性修改为4（已破产）
                UserPo userPo = (UserPo) session.getAttribute("userInfo");
                userPo.setStatus(4);
                session.setAttribute("userInfo", userPo);
                log.info("用户状态: {}", userPo.getStatus());
                return ResultUtil.success();
            }
            log.error("参数为空");
            return ResultUtil.error("参数为空");
        }catch (Exception e){
            log.error("停止经营失败: {}", e.getMessage());
            return ResultUtil.error();
        }
    }

    @Override
    public int findStatusByUserUnique(String userUnique) {
        return groupManageDao.findStatusByUserUnique(userUnique);
    }
}
