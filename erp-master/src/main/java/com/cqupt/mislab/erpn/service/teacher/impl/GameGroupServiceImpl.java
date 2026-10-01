package com.cqupt.mislab.erpn.service.teacher.impl;

import com.cqupt.mislab.erpn.commons.utils.AdOfStatusUtils;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.teacher.GameGroupInfoDao;
import com.cqupt.mislab.erpn.dao.user.ApprovedUserDao;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import com.cqupt.mislab.erpn.model.vo.advertisement.AdOfStatusVO;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupMemberStatus;
import com.cqupt.mislab.erpn.service.teacher.GameGroupService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author xhx
 * @Date 2021/9/8 13:05
 */
public class GameGroupServiceImpl implements GameGroupService {

    @Resource
    ApprovedUserDao approvedUserDao;
    @Resource
    GameGroupInfoDao gameGroupInfoDao;
    @Resource
    GroupManageDao groupManageDao;



    @Override
    public List<GameGroupInfo> showGameGroupsByTeacher(String adminId) {
         return new ArrayList<>();
    }

    @Override
    public List<RegisterUser> showGameGroupList(String groupName) {
        List<RegisterUser> groupMemberList = this.findGameGroupUserMessageByGroupName(groupName);
        return  groupMemberList;
    }

    @Override
    public List<RegisterUser> findGameGroupUserMessageByGroupName(String groupName) {
        RegisterUser creator = approvedUserDao.findGamegroupcreatorByGroupname(groupName);
        List<RegisterUser> list = approvedUserDao.findAllGamegroupuserByGroupname(groupName);

        List<RegisterUser> approvedUsers = new ArrayList<RegisterUser>(15);
        approvedUsers.add(creator);
        for(RegisterUser a : list){
            if(!a.getUserId().equals(creator.getUserId())){
                approvedUsers.add(a);
            }
        }

        return approvedUsers;
    }

    @Override
    public List<GameGroupMemberStatus> findGameGroupMemberStatusByGroupName(String groupName) {

        List<GameGroupMemberStatus> ggms=gameGroupInfoDao.findGameGroupMemberbygroupName(groupName);
        for(GameGroupMemberStatus g:ggms){
            int period= AdOfStatusUtils.computePeriod(g.getCurrentPeriod(), g.getYear(), g.getPeriodsOfOneYear());
            //将String转换为string类型
            //TODO 与数据库的类型不一致 xhx
            AdOfStatusVO asv= groupManageDao.findAdOfStatusByUseruniqueAndPeriod(g.getUserUnique(),String.valueOf(period));
            if(asv!=null){
                g.setChooseOrderFlag(asv.getChooseOrderFlag());
                g.setFinishAdFlag(asv.getFinishAdFlag());
                g.setFinishOrderFlag(asv.getFinishOrderFlag());
            }
        }
        return ggms;
    }
    //TODO 重构此方法
    @Override
    public boolean addHistory(String groupName) {
        Integer result = 0;

        return false;
    }

    @Override
    public boolean deleteGameGroupByGroupName(String groupName) {
        try {

            //TODO xhx 这里需要建立websocket连接
            gameGroupInfoDao.delete(GameGroupInfo.class, groupName);
            // 2015.1.15号修改
            // 这里主要是将推送消息的机制修改了.
            // 改成了 删除之后全部人 去加载 deleteGameGroup的方法
            // 同时在js上面加多一个方法.另外要在另外一个页面上面多加一个方法.因为 dwr找不到方法会报错
//            DWRPush.pushMessageWithFilter("deleteGameGroup", "",
//                    "showGameGroupMemberList" + groupName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int findYearsByGroupName(String groupName) {
        return gameGroupInfoDao.findYearByGroupname(groupName);
    }

    @Override
    public int findMembersOfInGroupByGroupName(String groupName) {
        return gameGroupInfoDao.findMembersOfInGroupByGroupname(groupName);
    }

    @Override
    public boolean isYearBegin(String userUnique) {
        GameGroupInfo gameGroupInfo = null;
        gameGroupInfo = gameGroupInfoDao.findGameGroupYearAndCurrentPeriod(userUnique);
        int currentPeriod = gameGroupInfo.getCurrentPeriod();// 当前期数
        int periodsOfOneYear = gameGroupInfo.getPeriodsOfOneYear();// 每年有多少期

        // 这里是解决一个bug：每年只有一个周期的时候除了第一年，后面都不用投广告了（杨青 2016/10/12 ）
        // newly begin
        if (periodsOfOneYear == 1) {
            return true;
        }
        // newly end

        if (currentPeriod % periodsOfOneYear == 1) {
            return true;
        }
        return false;
    }

    @Override
    public List<GameGroupInfo> showGameGroups(String adminID) {
        return gameGroupInfoDao.findAllGamegroup(adminID);
    }

    @Override
    public boolean addGameGroup(GameGroupInfo gameGoupInfo) {
        GameGroupMemberInfo gameGroupMember = new GameGroupMemberInfo();
        //TODO 需要ws xhx
        try {
//            gameGroupMember.setUserID(gameGoupInfo.getGroupCreaterId());
//            gameGroupMember.setGroupName(gameGoupInfo.getGroupName());
//            gameGroupDao.add(GameGroupInfo.class, gameGoupInfo);
//            gameGroupMemberDao.add(GameGroupMemberInfo.class, gameGroupMember);
           // DWRPush.refresh();
        } catch (Exception e) {
            // e.printStackTrace();
            System.out.println("创建报错");
            return false;
        }
        return true;
    }


}
