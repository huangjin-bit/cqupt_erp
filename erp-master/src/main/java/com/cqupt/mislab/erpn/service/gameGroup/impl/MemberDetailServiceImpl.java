package com.cqupt.mislab.erpn.service.gameGroup.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.group.GameGroupMemberDetailDao;
import com.cqupt.mislab.erpn.dao.teacher.GameGroupInfoDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.vo.gamegroup.GameGroupMemberDetailVo;
import com.cqupt.mislab.erpn.service.gameGroup.MemberDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/10/7 14:03
 */
@Service
@Slf4j
public class MemberDetailServiceImpl implements MemberDetailService {


    @Autowired
    private GameGroupMemberDetailDao gameGroupMemberDetailDao;
    @Autowired
    private GameGroupInfoDao gameGroupInfoDao;


    @Override
    public boolean confirmContribution(String groupName) {
        return gameGroupMemberDetailDao.confirmContribution(groupName);
    }

    @Override
    public GameGroupMemberDetail findMemberDetailByStudentNoUserUnique(String userUnique, String studentNo) {
        return gameGroupMemberDetailDao.findMemberDetailByStudentNo_userUnique(userUnique, studentNo);
    }

    @Override
    public List<GameGroupMemberDetail> listMemberDetailsUnordered(String userUnique) {
        return gameGroupMemberDetailDao.findAllInOneTeam(userUnique);
    }

    @Override
    public Result listMemberDetails(String userUnique) {
        List<GameGroupMemberDetail> allInOneTeam = gameGroupMemberDetailDao.findAllInOneTeam(userUnique);
        log.info("allInOneTeam:{}", allInOneTeam);
        List<GameGroupMemberDetailVo> res = allInOneTeam.stream().map((item) -> {
            GameGroupMemberDetailVo gameGroupMemberDetailVo = new GameGroupMemberDetailVo();
            gameGroupMemberDetailVo.setStudentName(item.getStudentName());
            gameGroupMemberDetailVo.setStudentNo(item.getStudentNo());
            gameGroupMemberDetailVo.setTitle(item.getTitle());
            gameGroupMemberDetailVo.setSort(item.getContribution().intValue());
            return gameGroupMemberDetailVo;
        }).sorted().collect(Collectors.toList());
        log.info("GameGroupMemberDetailVo:{}", res);
        return ResultUtil.success(res);
    }

    @Override
    public Result savaGameGroupMemberDetails(String userUnique, List<GameGroupMemberDetailVo> groupMemberDetailVos) {
//        1.根据数组下标设置排名
        int curMemberSize = groupMemberDetailVos.size();
        for (int i = 0; i < curMemberSize; i++) {
            groupMemberDetailVos.get(i).setSort(i + 1);
        }
        log.info("groupMemberDetailVos:{}", groupMemberDetailVos);
//        2.查询数据库中有多少组员
        List<GameGroupMemberDetail> allInOneTeam = gameGroupMemberDetailDao.findAllInOneTeam(userUnique);
        int tablesMemberSize = allInOneTeam.size();
//        3.若当前成员数量<数据库成员数量，则代表用户删除了一个人
        if (curMemberSize < tablesMemberSize) {
            log.info("当前成员数量<数据库成员数量，删除成员");
            removeMember(userUnique, allInOneTeam, groupMemberDetailVos);
        } else if (curMemberSize > tablesMemberSize) {   //若当前成员数量大于数据库成员数量，则代表用户添加了用户
            log.info("当前成员数量>数据库成员数量，添加成员");
            saveMemberDetail(userUnique, groupMemberDetailVos, allInOneTeam);
        }else {
            log.info("当前成员数量==数据库成员数量，修改排名");
            updateMemberDetail(userUnique, groupMemberDetailVos);
        }
        return ResultUtil.success(groupMemberDetailVos);
    }

    private void updateMemberDetail(String userUnique, List<GameGroupMemberDetailVo> groupMemberDetailVos) {
        for (GameGroupMemberDetailVo groupMemberDetailVo : groupMemberDetailVos) {
//            若oldStudentNo==“”,则代表没有修改学号，可能修改了其它的
            String title = groupMemberDetailVo.getTitle();
            String studentName = groupMemberDetailVo.getStudentName();
            String studentNo = groupMemberDetailVo.getStudentNo();
            float curSort = groupMemberDetailVo.getSort().floatValue();
            String oldStudentNo = groupMemberDetailVo.getOldStudentNo();
            log.info("oldStudentNo:{}", oldStudentNo);
            if (oldStudentNo.equals("")) {
                gameGroupMemberDetailDao.updateMemberDetail(userUnique, studentNo, studentName, title, curSort);
            } else {
                gameGroupMemberDetailDao.updateMemberDetailByOldStudentNo(userUnique, oldStudentNo, studentNo, title, studentName, curSort);
            }
        }

    }

    private void saveMemberDetail(String userUnique, List<GameGroupMemberDetailVo> groupMemberDetailVos, List<GameGroupMemberDetail> allInOneTeam) {
        List<GameGroupMemberDetailVo> waitAddMember = new ArrayList<>();
//        先将数据库成员里的所有STUDENTNO拿出来，然后在对比
        List<String> inDataInfo = new ArrayList<>();
        for (GameGroupMemberDetail groupMemberDetail : allInOneTeam) {
            inDataInfo.add(groupMemberDetail.getStudentNo());
        }
//      1.取出数据库中没有的成员
        for (GameGroupMemberDetailVo groupMemberDetailVo : groupMemberDetailVos) {
            if (!inDataInfo.contains(groupMemberDetailVo.getStudentNo())) {
                waitAddMember.add(groupMemberDetailVo);
            }
        }
        log.info("当前成员：{}，数据库成员：{}，待添加的成员：{}", groupMemberDetailVos, allInOneTeam, waitAddMember);

//       2.添加成员
        GameGroupInfo gameGroup = gameGroupInfoDao.findGamegroupByUserunique(userUnique);
        /*
         * .这里获取小组的创建者userId，然后再插入到表中。感觉不是很合理，
         *  感觉应该是获取每个企业的userId，
         */
        //此处添加是为了解决在教师端创建的比赛不能添加小组成员
        String userId = gameGroup.getGroupCreatorId();
        if (userId==null||StringUtils.isEmpty(userId)) {
            userId = "T" + gameGroup.getTeacherId();
        }
        for (GameGroupMemberDetailVo gameGroupMemberDetailVo : waitAddMember) {
            GameGroupMemberDetail groupMemberDetail = new GameGroupMemberDetail();
            groupMemberDetail.setTitle(gameGroupMemberDetailVo.getTitle());
            groupMemberDetail.setContribution(gameGroupMemberDetailVo.getSort().floatValue());
            groupMemberDetail.setGroupName(gameGroup.getGroupName());
            groupMemberDetail.setStudentName(gameGroupMemberDetailVo.getStudentName());
            groupMemberDetail.setStudentNo(gameGroupMemberDetailVo.getStudentNo());
            groupMemberDetail.setUserUnique(userUnique);
            groupMemberDetail.setUserId(userId);
            gameGroupMemberDetailDao.insertMemberDetail(groupMemberDetail);
        }
//        添加新成员后，可能会出现贡献排名的变化，所以需调用以此修改
        updateMemberDetail(userUnique, groupMemberDetailVos);

    }

    private void removeMember(String userUnique,List<GameGroupMemberDetail> allInOneTeam, List<GameGroupMemberDetailVo> groupMemberDetailVos) {
        // 待删除人的学号
        List<String> waitRemoveNos = new ArrayList<>();
//        取出当前所有成员的学号
        List<String> curMembers = new ArrayList<>();
        for (GameGroupMemberDetailVo groupMemberDetailVo : groupMemberDetailVos) {
            curMembers.add(groupMemberDetailVo.getStudentNo());
        }
        for (GameGroupMemberDetail groupMemberDetail : allInOneTeam) {
            if (!curMembers.contains(groupMemberDetail.getStudentNo())) {
                waitRemoveNos.add(groupMemberDetail.getStudentNo());
            }
        }
//        若待删除人的学号不等于空则删除该成员
        for (String waitRemoveNo : waitRemoveNos) {
            gameGroupMemberDetailDao.deleteMember(userUnique,waitRemoveNo);
        }
//        删除过后更新数据库成员的排名
        updateMemberDetail(userUnique, groupMemberDetailVos);
    }
}
