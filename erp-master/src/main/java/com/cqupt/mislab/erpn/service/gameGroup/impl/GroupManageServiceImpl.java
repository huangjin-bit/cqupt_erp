package com.cqupt.mislab.erpn.service.gameGroup.impl;

import com.alibaba.fastjson.JSONObject;
import com.cqupt.mislab.erpn.commons.constant.*;
import com.cqupt.mislab.erpn.commons.utils.AdOfStatusUtils;
import com.cqupt.mislab.erpn.commons.websocket.CommonWebsocketServicePublisher;
import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import com.cqupt.mislab.erpn.dao.advertisement.AdOfStatusVODao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementDao;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.teacher.GameGroupInfoDao;
import com.cqupt.mislab.erpn.dao.user.LoginUserDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.GroupCreateDto;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.model.vo.advertisement.AdOfStatusVO;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupMemberStatus;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementStatusOfUserService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupMemberService;
import com.cqupt.mislab.erpn.service.gameGroup.IniGameService;
import com.cqupt.mislab.erpn.service.order.ChooseOrderService;
import com.cqupt.mislab.erpn.service.user.UserService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.utils.WsResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.StringUtils;

import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: com.cqupt.mislab.erpn.service.impl
 * @description:
 * @author: keji0
 * @date: 2020/8/10 8:51
 */
@Service
@Slf4j
public class GroupManageServiceImpl implements GroupManageService {

    @Autowired
    UserService userService;
    @Autowired
    CommonDao commonDao;
    @Autowired
    IniGameService iniGameService;
    @Autowired
    AdvertisementService advertisementService;
    @Autowired
    CommonWebsocketServicePublisher websocketServicePublisher;
    @Autowired
    GameGroupInfoDao gameGroupInfoDao;
    @Autowired
    ChooseOrderService chooseOrderService;
    @Autowired
    LoginUserDao loginUserDao;
    @Autowired
    GroupManageDao groupManageDao;
    @Autowired
    AdOfStatusVODao adOfStatusVODao;
    @Autowired
    WebsocketEndPoint websocketEndPoint;
    @Autowired
    AdvertisementDao advertisementDao;
    @Autowired
    AdvertisementStatusOfUserService advertisementStatusOfUserService;
    @Autowired
    AdAndOrderRefreshService adAndOrderRefreshService;
    @Autowired
    GroupMemberService groupMemberService;

    @Override
    public List<GameGroupInfo> findUserGroupInfoByVagueName(String vagueName, String adminId) {
        List<GameGroupInfo> gameGroupByUserVague = groupManageDao.findGameGroupByUserVague(adminId, vagueName);
        //转化成年数
        //TODO 抽象成一个方法
        for(GameGroupInfo info : gameGroupByUserVague){
            if( info.getCurrentPeriod() % 4 == 0){
                info.setYears(1 + info.getCurrentPeriod() / 4 - 1);
            }else{
                info.setYears(1 + info.getCurrentPeriod() / 4);
            }
            info.setYears(1 + info.getYears() % 4);
        }

        return gameGroupByUserVague;
    }

    @Override
    public List<GameGroupInfo> findTeacherGroupInfoByVagueName(String vagueName, String adminId) {
        List<GameGroupInfo> gameGroupByUserVague = groupManageDao.findGameGroupByTeacherVague(adminId, vagueName);
        //转化成年数
        for(GameGroupInfo info : gameGroupByUserVague){
            if( info.getCurrentPeriod() % 4 == 0){
                info.setYears(1 + info.getCurrentPeriod() / 4 - 1);
            }else{
                info.setYears(1 + info.getCurrentPeriod() / 4);
            }
            info.setYears(1 + info.getYears() % 4);
        }

        return gameGroupByUserVague;
    }

    @Override
    public int setGradeRange(String groupName, int low, int high) {
        GameGroupInfo gamegroupByGroupname = gameGroupInfoDao.findGamegroupByGroupname(groupName);
        gamegroupByGroupname.setMaxScore((float) high);
        gamegroupByGroupname.setMinScore((float)low);
        return gameGroupInfoDao.setGradeProportion(gamegroupByGroupname);
    }

    @Override
    public int endPlayGame(String groupName, String userUnique) {
        try {
            // 这里开始做广告单的清算
            advertisementService.updateAdvertisementFinish(userUnique);
            // 先判断是否已经破产, 如果已经破产了就不用做这个了.
            int status = groupMemberService.findStatusByUserUnique(userUnique);
            if (status != 0) {
                if (isYearBegin(userUnique)) {
                    Integer currentPeriod = gameGroupInfoDao.findCurrentTime(userUnique);
                    // 判断是否可以结束游戏
                    double result = advertisementDao.findSummaryMoney(userUnique,
                            currentPeriod);
                    if (result != 0&& !advertisementStatusOfUserService.findChooseOrderFlag(userUnique, currentPeriod)) {

                        return 0;
                    }
                    // 完成这里的所有投放广告.
                    advertisementService.updateAdvertisementFinish(userUnique);

                    // 判断选单是否完成
                    if (!advertisementStatusOfUserService
                            .findFinishOrderFlag(userUnique)) {
                        // 结束选单
                        chooseOrderService.updateEndChooseOrder(userUnique);
                        // 通知别人选单
                        adAndOrderRefreshService.changeOrder(userUnique);
                        //DWRPush.refresh();
                    }

                }
                groupManageDao.updateBankruptcyUserStatus(userUnique, 0);

                //判断是否需要生成订单--周军
                //这个是周军的代码，后来杨青让韩雨潇修改了接口名称。接口原本名文isCheckProductOrders
                //修改为
                if(advertisementService.checkGenerateOrderIfNecessary(userUnique)){
                    //TODO xhx ws通知
                    //DWRPush.refresh();
                }
            }
        } catch (Exception e) {
            System.out.println("结束游戏出错");
            return 0;
        }

        return 1;
        /*
         * if(status != 0){ GameGroupInfo gameGroupInfo = new GameGroupInfo();
         * gameGroupInfo =
         * gameGroupDao.findGameGroupYearAndCurrentPeriod(userUnique); int A =
         * gameGroupInfo.getCurrentPeriod(); //当前期数 int B =
         * gameGroupInfo.getPeriodsOfOneYear(); //每年的期数 int C =
         * gameGroupInfo.getYears(); //年数 int D = 1; //计算出的每年开始的第一期
         * //System.out.println("A=" + A +"     B:"+ B +"    C:"+ C + "    D:" +
         * D); while(true) { if( D>A && D<=(C-1)*B + 1){
         * insertDataToAdststusofuser(userUnique,D); } if(D>(C-1)*B+1){ break ;
         * }else{ D = D+B; //继续执行if语句 } } }
         */
    }

    @Override
    public List<GameGroupMemberStatus> findGameGroupMemberStatusByGroupName(String groupName) {
        List<GameGroupMemberInfo> gameGroupMemberInfos = gameGroupInfoDao.findGameGroupMemberList(groupName);
        // 先判断是否开始游戏（status=10表示还没开始）

        if (!gameGroupMemberInfos.isEmpty()
                && gameGroupMemberInfos.get(0).getStatus() == 10) {

            return gameGroupInfoDao.findGameGroupMemberStatusbygroupNamewithoutStartGame(groupName);
        } else {

            return findGameGroupMemberStatusByGroupName2(groupName);
        }
    }
    public List<GameGroupMemberStatus> findGameGroupMemberStatusByGroupName1(String gameGroup){
        return gameGroupInfoDao.findGameGroupMemberbygroupName(gameGroup);
    }
    public List<GameGroupMemberStatus> findGameGroupMemberStatusByGroupName2(String gameGroup){
        List<GameGroupMemberStatus> ggms = findGameGroupMemberStatusByGroupName1(gameGroup);
        for(GameGroupMemberStatus g: ggms){
            int period = AdOfStatusUtils.computePeriod(g.getCurrentPeriod(), g.getYear(), g.getPeriodsOfOneYear());

            AdOfStatusVO asv = adOfStatusVODao.findAdOfStatusByUseruniqueAndPeriod(g.getUserUnique(), String.valueOf(period));
            if(asv != null){
                g.setChooseOrderFlag(asv.getChooseOrderFlag());
                g.setFinishAdFlag(asv.getFinishAdFlag());
                g.setFinishOrderFlag(asv.getFinishOrderFlag());
            }

        }
        return  ggms;
    }
    @Override
    public List<GameGroupInfo> showHistoryGameGroups(String admin) {
        return gameGroupInfoDao.findHisGameGroup(admin);
    }

    @Override
    public List<GameGroupInfo> showGameGroupByUser(String adminId) {
        List<GameGroupInfo> gameGroupByUser = groupManageDao.findGameGroupByUser(adminId);
        //转化成年数
        for(GameGroupInfo info : gameGroupByUser){
            //System.out.println("当前组的Execllent" + info.getExcellent());
            if( info.getCurrentPeriod() % 4 == 0){
                info.setYears(1 + info.getCurrentPeriod() / 4 - 1);
            }else{
                info.setYears(1 + info.getCurrentPeriod() / 4);
            }
        }
        return  gameGroupByUser;

    }

    @Override
    public List<GameGroupInfo> showGameGroupByTeacher(String adminId) {
        List<GameGroupInfo> gameGroupInfoByTeacher = groupManageDao.findGameGroupInfoByTeacher(adminId);
        //转化成年数
        for(GameGroupInfo info : gameGroupInfoByTeacher){
            if( info.getCurrentPeriod() % 4 == 0){
                info.setYears(1 + info.getCurrentPeriod() / 4 - 1);
            }else{
                info.setYears(1 + info.getCurrentPeriod() / 4);
            }
        }
        return  gameGroupInfoByTeacher;
    }

    /**
     * 创建一个分组，并向组内广播创建人、组名等信息
     * @param groupCreateDto
     * @param session
     * @return gameGroupInfo，返回这组的详细信息
     */
    @Override
    public Result createGroup(GroupCreateDto groupCreateDto, HttpSession session) {
        /**
         * 获取当前用户的userId
         * 并通过userId查询该用户的登录状态
         * 正常情况下状态码应该为2（用户已经被授权，且没有加入游戏分组）
         * 若不是2则返回error
         */
        String groupCreatorId =  (String)session.getAttribute("userId");
        int loginCode = userService.loginStatus(groupCreatorId);
        if (loginCode != 2){
            log.error("{}正在游戏中",groupCreatorId);
            return ResultUtil.error(groupCreatorId+"正在游戏中");
        }

        if (groupCreateDto.getMaxEnterpriseNumber() > 50 || groupCreateDto.getMaxEnterpriseNumber() < 1){
            log.error("企业数量应为1-50");
            return ResultUtil.error("企业数量应为1-50");
        }

        //获取组创建者的userId

        log.info("组创建者是{}",groupCreatorId);

        try {
            //把组的详细信息封装在GameGroupInfo类中
            GameGroupInfo gameGroupInfo = new GameGroupInfo();
            gameGroupInfo.setGroupName(groupCreateDto.getGroupName());
            gameGroupInfo.setTeacherId(groupCreateDto.getTeacherId());
            gameGroupInfo.setUserNumbers(groupCreateDto.getMaxEnterpriseNumber());
            gameGroupInfo.setGroupCreatorId(groupCreatorId);

            //根据gameGroupInfo增加游戏分组
            boolean result = addGameGroup(gameGroupInfo);
            log.info("增加一个分组：{}",result);

            /**
             * 若增加游戏分组成功则把这个组的组名（groupName）存入session中
             * 并向未加入小组的人广播该小组有关信息
             */
            if (result){
                String groupName = gameGroupInfo.getGroupName();
                if (session.getAttribute("groupName") != null){
                    session.removeAttribute("groupName");
                }
                session.setAttribute("groupName",groupName);
                List<GameGroupMemberInfo> groupMemberInfos = loginUserDao.findGroupMembersByGroupName(groupName);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("groupMessage",gameGroupInfo);
                map.put("groupMemberList",groupMemberInfos);
                //更新userInfo状态
                UserPo userPo = (UserPo)session.getAttribute("userInfo");
                userPo.setStatus(7);
                session.setAttribute("userInfo", userPo);

                //给所有未加入分组者广播创建人、组名等信息
                String message = JSONObject.toJSONString(WsResultUtil.UpdateGroup(groupCreatorId + GroupManageConstant.GROUP_CREATE_COMPLETE + groupName));
                log.info("向{}组广播websocket消息:{}",GroupManageConstant.NO_GROUP,message);
                websocketEndPoint.publish(GroupManageConstant.NO_GROUP,new TextMessage(message));
                return ResultUtil.success(map);
            }else {
                log.info("创建分组失败");
                return ResultUtil.error("创建分组失败");
            }
        }catch (Exception e){
            log.error("创建报错：{}",e);
            return ResultUtil.error("创建报错");
        }

    }

    /**
     * 根据给定的分组信息（GameGroupInfoDao）创建游戏分组，相当于把增加游戏分组的SQL独立写了一个方法，其它业务逻辑的判断在createGroup方法中
     * @param gameGoupInfo
     * @return boolean
     */
    @Override
    public boolean addGameGroup(GameGroupInfo gameGoupInfo) {
        GameGroupMemberInfo gameGroupMember = new GameGroupMemberInfo();
        try {
            //把组员与组的关联信息封装在GameGroupMemberInfo类中（这里组员特指组创建者groupCreator）
            String groupCreatorId = gameGoupInfo.getGroupCreatorId();
            gameGroupMember.setUserId(groupCreatorId);
            gameGroupMember.setGroupName(gameGoupInfo.getGroupName());
            /**
             * 在一个游戏分组被创建时，对两个数据表进行了insert操作
             * 1.在保存所有游戏分组信息的表（gamegroup）中增加了当前被创建的组的一行数据
             * 2.在保存"组员---组"的关联信息的表（gamegroupmember）中增加了第一个组成员的数据，也即是组创建者的数据
             */
            groupManageDao.addGroup(gameGoupInfo.getGroupName(),groupCreatorId,gameGoupInfo.getTeacherId(),gameGoupInfo.getUserNumbers(),gameGoupInfo.getYears(),gameGoupInfo.getPeriodsOfOneYear());
            groupManageDao.addGroupMember(gameGroupMember.getGroupName(),gameGroupMember.getUserId());

        } catch (Exception e) {
            log.error("创建报错：{}",e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 获取教师列表
     * @return Result
     */
    @Override
    public Result getTeacherList() {
        try{
            return ResultUtil.success(groupManageDao.getTeacherList());
        }catch (Exception e){
            log.error("异常：{}",e);
            return ResultUtil.error("异常");
        }

    }

    /**
     * 获取教师的id列表
     * @return Result
     */
    @Override
    public Result getTeacherIdList() {
        try{
            return ResultUtil.success(groupManageDao.getTeacherIdList());
        }catch (Exception e){
            log.error("异常：{}",e);
            return ResultUtil.error("异常");
        }
    }

    @Override
    @Transactional(rollbackFor = IllegalArgumentException.class)
    public Result startGameByTeacher(GameGroupInfo gameGroupInfo) throws Exception {
        Boolean res = false;
        GameGroupInfo groupByGroupName = groupManageDao.findGroupByGroupName(gameGroupInfo.getGroupName());
        if(groupByGroupName == null){
            groupManageDao.addGroup(gameGroupInfo.getGroupName(), null, gameGroupInfo.getTeacherId(), gameGroupInfo.getUserNumbers(), gameGroupInfo.getYears(), gameGroupInfo.getPeriodsOfOneYear());
        }else{
            return ResultUtil.error(TeacherImportGamesEnum.GROUPNAME_REAPTED.getStatus(),TeacherImportGamesEnum.GROUPNAME_REAPTED.getMessage());
        }
        String[] userId = gameGroupInfo.getUserIds().split(",");
        Map<Boolean, List<String>> collect = Arrays.stream(userId).collect(Collectors.partitioningBy(userService::isGameRunning));
        List<String> running = collect.get(true);
        if(running.size() != 0){
            throw new IllegalArgumentException(running.toString());
        }
        for(int i = 0; i < userId.length; i++){
            groupManageDao.addGroupMember(gameGroupInfo.getGroupName(),userId[i]);
        }

        return startGameByTeacher(gameGroupInfo.getGroupName());
    }

    @Override
    public Result startGameByTeacher(String groupName) throws Exception {
        GameGroupInfo groupMessage = groupManageDao.findGroupByGroupName(groupName);
        if (groupMessage.getCurrentPeriod() > 0) {
            return ResultUtil.error("游戏已开始");
        }
        String errorMsg = null;
        boolean iniSQL = iniGameByPLSQL(groupName);

        boolean iniPrediction = initPredictionOfGroup(groupName);
        boolean iniOrder = iniGroupOrder(groupName);
        boolean iniISO = iniOrderISO(groupName);
        if (iniISO && iniOrder && iniPrediction && iniSQL){
            if (!addMemberIntoMemberDetailTable(groupName)) {
                return ResultUtil.error("初始化组员详情失败");
            }
            //ws操作  需要的广播信息
            //String userUnique = commonDao.findUserUniqueByUserId(userId);
            //log.info("userUnique: {}", userUnique);
            //session.setAttribute("userUnique", userUnique);
            //String message = JSONObject.toJSONString(WsResultUtil.startGame(groupMessage, userId+"开始"+groupName+"比赛"));
//            log.info(message);
//            websocketServicePublisher.publish(groupName, new TextMessage(message));
        }else{
            return ResultUtil.error(errorMsg);
        }

        return ResultUtil.success(groupMessage);
    }


    /**
     * 获取分组列表
     * @return Result
     */
    @Override
    public Result getGroupList() {
        try {
            return ResultUtil.success(groupManageDao.getGroupList());
        }catch (Exception e){
            log.error("异常：{}",e);
            return ResultUtil.error("异常");
        }

    }

    /**
     * 获取指定分组内的成员列表
     * @param groupName
     * @param session
     * @return Result
     */
    @Override
    public Result getGroupMemberList(String groupName, HttpSession session) {
        try {
            List<ApprovedUser> groupMemberList = findGroupUserMessageByGroupName(groupName);
            if (groupMemberList == null) {
                return ResultUtil.error("比赛结束");
            } else {
                return ResultUtil.success(groupMemberList);
            }
        }catch (Exception e){
            log.error("异常：{}",e);
            return ResultUtil.error("异常");
        }

    }


    /**
     * @param groupName
     * @descriptin 通过分组名称获取当前分组下的用户信息（审批表中，非分组成员表）
     * @return
     */
    @Override
    public List<ApprovedUser> findGroupUserMessageByGroupName(String groupName) {
        try {
            ApprovedUser creator = loginUserDao.findGroupCreatorUserByGroupName(groupName);
            List<ApprovedUser> list = loginUserDao.findGroupUserMessageByGroupName(groupName);
            List<ApprovedUser> approvedUsers;
            approvedUsers = new ArrayList<ApprovedUser>();
            //注意:approvedUsers数组有一定的顺序
            //组创建者作为数组第一个元素
            if(creator!=null){
//                若creator不等于null意味着是学生创建分组
                approvedUsers.add(creator);
                for (ApprovedUser approvedUser : list){
                    if (!approvedUser.getUserId().equals(creator.getUserId())){
                        approvedUsers.add(approvedUser);
                    }
                }
            }else{
//                若creator为null则代表是老师创建分组
                approvedUsers.addAll(list);
            }
            return approvedUsers;
        }catch (Exception e){
            log.error("异常：{}",e);
            return null;
        }

    }

    /**
     * 获取给定组的所有"组员---组"对应关系，保存在list中返回
     * @param groupName
     * @param session
     * @return
     */
    @Override
    public Result findGroupMembersByGroupName(String groupName,HttpSession session) {
        try {
            //TODO
            String userId = (String)session.getAttribute("userId");
            List<GameGroupMemberInfo> gameGroupMemberInfoList = loginUserDao.findGroupMembersByGroupName(groupName);
            return ResultUtil.success(gameGroupMemberInfoList);
        } catch (Exception e) {
            log.error("异常：{}",e);
            return ResultUtil.error("");
        }
    }

    @Override
    public LoginResponse getStudentBasicInfo(HttpSession session) {
        try {
            String userId = (String) session.getAttribute("userId");
            String groupName = (String) session.getAttribute("groupName");
            UserPo userPo = (UserPo) session.getAttribute("userInfo");
            int loginStatus = userPo.getStatus();
            userPo.setRoleByStatus(loginStatus);
            Map<String, Object> map = new HashMap<>();
            map.put("userMessage", userPo);
            switch (loginStatus) {
                case 1:
                    return new LoginResponse<>(LoginCode.REGISTERUSER, LoginMessage.REGISTERUSER, map);
                case 2:
                    return new LoginResponse<>(LoginCode.WRONG_PASSWORD, LoginMessage.WRONG_PASSWORD, null);
                case 3:
                    session.setAttribute("groupName", "NoGroup");
                    List<GameGroupInfo> groupList = groupManageDao.getGroupList();
                    map.put("groupList", groupList);
                    return new LoginResponse<>(LoginCode.NO_GAMEGROUP, LoginMessage.NO_GAMEGROUP, map);
                case 4:
                    return new LoginResponse<>(LoginCode.RUIN, LoginMessage.RUIN, map);
                case 5:
                    return new LoginResponse<>(LoginCode.IN_GAME, LoginMessage.IN_GAME, map);
                case 6:
                    return new LoginResponse<>(LoginCode.GAME_COMPLETE, LoginMessage.GAME_COMPLETE, map);
                case 7:
                    GameGroupInfo gameGroupInfo_creator = loginUserDao.findGameGroupByGroupName(groupName);
                    map.put("groupMessage", gameGroupInfo_creator);
                    List<GameGroupMemberInfo> gameGroupMemberInfoList_creator = loginUserDao.findGroupMembersByGroupName(groupName);
                    map.put("groupMemberList", gameGroupMemberInfoList_creator);
                    return new LoginResponse<>(LoginCode.GAMEGROUP_CREATOR, LoginMessage.GAMEGROUP_CREATOR, map);
                case 8:
                    GameGroupInfo gameGroupInfo_member = loginUserDao.findGameGroupByGroupName(groupName);
                    map.put("groupMessage", gameGroupInfo_member);
                    List<GameGroupMemberInfo> gameGroupMemberInfoList_member = loginUserDao.findGroupMembersByGroupName(groupName);
                    map.put("groupMemberList", gameGroupMemberInfoList_member);
                    return new LoginResponse<>(LoginCode.GAMEGROUP_MEMBER, LoginMessage.GAMEGROUP_MEMBER, map);
                case -1:
                    return new LoginResponse<>(LoginCode.NOT_REGISTER, LoginMessage.NOT_REGISTER, null);
                default:
                    return new LoginResponse<>(LoginCode.ILLEGAL, LoginMessage.ILLEGAL, null);
            }
        } catch (Exception e) {
            log.error("异常：{}", e);
            return new LoginResponse(LoginCode.ILLEGAL, "后台异常", null);
        }
    }

    /**
     * 把当前用户的信息、当前用户所在组的信息、以及他组内其他人的信息放入list中返回
     * @param session
     * @return
     */
//    @Override
//    public UserMessage getStudentBasicInfo(HttpSession session) {
//        try {
//            String userId = (String)session.getAttribute("userId");
//            String groupName = (String)session.getAttribute("groupName");
//            UserPo userPo = (UserPo)session.getAttribute("userInfo");
//            int loginStatus = userPo.getStatus();
//            userPo.setRoleByStatus(loginStatus);
//            String role = userPo.getRole();
//                    //获取当前用户所在组的详细信息
//            GameGroupInfoDao gameGroupInfo = loginUserDao.findGameGroupByGroupName(groupName);
//            //获取当前用户所在组所有组员详细信息
//            List<GameGroupMemberInfo> gameGroupMemberInfoList = loginUserDao.findGroupMembersByGroupName(groupName);
//            //获取当前用于的信息
//            ApprovedUser approvedUser = loginUserDao.findApprovedUserByUserId(userId);
//            //获取分组列表
//            List<GameGroupInfoDao> groupList = groupManageDao.getGroupList();
//            //把当前用户所在组的信息、以及他组内其他人的信息放入map中返回
//            Map<String, Object> map = new HashMap<>();
//            map.put("userMessage", approvedUser);
//            map.put("groupList", groupList);
//            map.put("gameGroupInfo", gameGroupInfo);
//            map.put("groupMemberList", gameGroupMemberInfoList);
//            return new UserMessage(loginStatus, role, map);
//        }catch (Exception e){
//            log.error("异常：{}",e);
//            return new UserMessage(null,"后台异常",null);
//        }
//    }


    /**
     * 获取给定组的组创建者信息
     * @param groupName
     * @return
     */
    @Override
    public Result getGroupCreator(String groupName) {
        try {
            ApprovedUser creator = loginUserDao.findGroupCreatorUserByGroupName(groupName);
            return ResultUtil.success(creator);
        }catch (Exception e){
            log.error("异常：{}",e);
            return ResultUtil.error("后台异常");
        }
    }

    //TODO 用ws给前端推送消息,设置iniGame（参考老erp的IniGameService和IniGameServiceImpl
    @Override
  public Result startGame(String groupName, HttpSession session) throws Exception {
        UserPo userPo = (UserPo) session.getAttribute("userInfo");
        String userId = userPo.getUserId();
        GameGroupInfo groupMessage = groupManageDao.findGroupByGroupName(groupName);


        if (groupMessage.getCurrentPeriod() > 0) {
            return ResultUtil.error("游戏已开始");
        } else if (!userService.isGameCreator(userId)) {
            return ResultUtil.error("不是分组创建者");
        }

        String errorMsg = null;
        boolean iniSQL = iniGameByPLSQL(groupName);

        boolean iniPrediction = initPredictionOfGroup(groupName);
        boolean iniOrder = iniGroupOrder(groupName);
        boolean iniISO = iniOrderISO(groupName);
        //        修正：初始化游戏时，向groupmemberdetail添加为每个企业（即每位组员）的信息
        addMemberIntoMemberDetailTable(groupName);
        if (iniISO && iniOrder && iniPrediction && iniSQL){
            String userUnique = commonDao.findUserUniqueByUserId(userId);
            log.info("userUnique: {}", userUnique);
            session.setAttribute("userUnique", userUnique);
            String message = JSONObject.toJSONString(WsResultUtil.startGame(groupMessage, userId+"开始"+groupName+"比赛"));
            log.info(message);
            websocketServicePublisher.publish(groupName, new TextMessage(message));

        }else{
            return ResultUtil.error(errorMsg);
        }

        return ResultUtil.success(groupMessage);
    }

    @Override
    public boolean addMemberIntoMemberDetailTable(String groupName) {
        if (groupName == null || StringUtils.isEmpty(groupName)) {
            log.error("groupName:{}", groupName);
            return false;
        }
        List<GameGroupMemberDetail> groupMemberDetails = gameGroupInfoDao.findGameGroupMemberDTO(groupName);
        log.info("GameGroupMemberDetail：{}",groupMemberDetails);
        for (GameGroupMemberDetail groupMemberDetail : groupMemberDetails) {
            if (gameGroupInfoDao.findMemberDetailByStudentNoAndUserUnique(groupMemberDetail.getUserUnique(), groupMemberDetail.getStudentNo()) == null) {
                groupMemberDetail.setContribution(1.0f);
                groupMemberDetail.setTitle("ceo");
                gameGroupInfoDao.addMemberDetail(groupMemberDetail);
            }
        }
        return true;
    }


    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,Error.class})
    @Override
    public boolean iniGameByPLSQL(String groupName) throws Exception {
        boolean ini = iniGameService.iniGameByPLSQL(groupName);
        System.out.println(ini);
        if (ini) {
            return true;
        }else {
            throw new RuntimeException("初始化数据库失败..");
        }
    }

    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,Error.class})
    @Override
    public boolean initPredictionOfGroup(String groupName) throws Exception {
        boolean ini = iniGameService.initPredictionOfGroup(groupName);
        System.out.println(ini);
        if (ini) {
            return true;
        }else {
            throw new RuntimeException("初始化市场预测信息失败..");
        }
    }

    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,Error.class})
    @Override
    public boolean iniGroupOrder(String groupName) throws Exception {
        boolean ini = iniGameService.iniGroupOrder(groupName);
        System.out.println(ini);
        if (ini) {
            return true;
        }else {
            throw new RuntimeException("初始化市场订单失败..");
        }
    }

    @Transactional(rollbackFor = {Exception.class,RuntimeException.class,Error.class})
    @Override
    public boolean iniOrderISO(String groupName) throws Exception {
        boolean ini = iniGameService.iniOrderISO(groupName);
        System.out.println(ini);
        if (ini) {
            return true;
        }else {
            throw new RuntimeException("初始化ISO失败..");
        }
      }

    @Override
    public Result deleteGameGroupByGroupName(String groupName,HttpSession session) {
//        1.验证传入的groupName是否为空
        if (StringUtils.isEmpty(groupName)) {
            log.error("groupName={}",groupName);
        }
        UserPo userPo = (UserPo) session.getAttribute("userInfo");
//        验证是否是房主
        if (!userService.isGameCreator(userPo.getUserId())) {
            log.info("userId={},该用户不是房主", userPo.getUserId());
        }else {
//        2.根据组名获取组信息
            GameGroupInfo gameGroupInfo = groupManageDao.findGroupByGroupName(groupName);
            if (gameGroupInfo == null) {
                log.error("gameGroup={},不存在", gameGroupInfo);
            }
            String groupCreatorId = gameGroupInfo.getGroupCreatorId();
//        3.删除组名为groupName的小组，如果成功则广播消息
            Boolean removeGroup = groupManageDao.deleteGameGroupByGroupName(groupName);
//            Boolean removeMembers=groupManageDao.deleteGroupMembersByGroupName(groupName);
            log.info("removeGroup={}，removeMembers={}", removeGroup);

            if (removeGroup) {

                String message1 = JSONObject.toJSONString(WsResultUtil.deleteGroup());
                //对该组进行推送
                websocketEndPoint.publish(groupName, new TextMessage(message1));

                //24.6.15新增：给所有未加入分组者广播创建人解散组名信息
                String message2 = JSONObject.toJSONString(WsResultUtil.deleteGroup(groupCreatorId + GroupManageConstant.GROUP_DELETE_SUCCESS + groupName));
                log.info("向{}组广播websocket消息:{}",GroupManageConstant.NO_GROUP,message2);
                websocketEndPoint.publish(GroupManageConstant.NO_GROUP,new TextMessage(message2));

                websocketEndPoint.deleteGroup(groupName, session);
                return ResultUtil.success();
            }
        }
        return ResultUtil.error();
    }

    @Override
    public synchronized boolean isYearBegin(String userUnique) {

        int currentPeriod = commonDao.findCurrentPeriod(userUnique);//4 (4-1)/4+1=1 4-(0)*?=4
        log.info("当前用户：{}，isYearBegin:{}", userUnique, currentPeriod);

        Integer periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        Integer currentYear = (currentPeriod - 1) / periodsOfOneYear + 1;
        //当年的第几周期
        Integer currentPeriodOfYear = currentPeriod - (currentYear - 1) * periodsOfOneYear;
        log.info("currentPeriodOfYear:{}", currentPeriodOfYear);
        return currentPeriodOfYear > 1 ? false : true;//大于1则返回false
    }

    @Override
    public Result modifySessionStatus(HttpSession session) {
        UserPo userInfo = (UserPo) session.getAttribute("userInfo");
        String userUnique = commonDao.findUserUniqueByUserId(userInfo.getUserId());
        log.info("modifySessionStatus");
        userInfo.setStatus(5);
        session.setAttribute("userInfo", userInfo);
        session.setAttribute("userUnique", userUnique);
        return ResultUtil.success();
    }
}
