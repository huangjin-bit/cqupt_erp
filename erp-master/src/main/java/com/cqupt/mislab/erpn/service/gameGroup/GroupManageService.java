package com.cqupt.mislab.erpn.service.gameGroup;

import com.cqupt.mislab.erpn.commons.constant.LoginResponse;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.GroupCreateDto;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupMemberStatus;
import org.springframework.data.repository.query.Param;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2020/8/10 8:51
 */
public interface GroupManageService {

    List<GameGroupInfo> findUserGroupInfoByVagueName(String vagueName, String adminId);

    List<GameGroupInfo> findTeacherGroupInfoByVagueName(String vagueName, String adminId);

    int setGradeRange(String groupName, int low, int high);

    int endPlayGame(String groupName, String userUnique);

    List<GameGroupMemberStatus> findGameGroupMemberStatusByGroupName(String admin);

    /**
     * 展示历史信息
     * @param admin
     * @return
     */
    List<GameGroupInfo> showHistoryGameGroups(String admin);

    List<GameGroupInfo> showGameGroupByUser(String adminId);
    /**
     * 教师端管理分组模块
     * 某个老师所建立的分组
     * @param adminId
     * @return
     */
    List<GameGroupInfo> showGameGroupByTeacher(String adminId);

    Result createGroup(GroupCreateDto groupCreateDto, HttpSession session);

    boolean addGameGroup(GameGroupInfo gameGoupInfo);

    Result getTeacherList();

    Result getTeacherIdList();
    Result startGameByTeacher(GameGroupInfo gameGroupInfo) throws Exception;
    Result startGameByTeacher(String groupName) throws Exception;
    Result getGroupList();

    Result getGroupMemberList(@Param("groupName")String groupName, HttpSession session);

    List<ApprovedUser> findGroupUserMessageByGroupName(@Param("groupName")String groupName);

    Result findGroupMembersByGroupName(@Param("groupName")String groupName,HttpSession session);

    LoginResponse getStudentBasicInfo(HttpSession session);

    Result getGroupCreator(@Param("groupName") String groupName);

    Result startGame(String groupName, HttpSession session) throws Exception;

    /**
     * 向gamegroupmemberdetail初始化信息
     * @param groupName
     * @return
     */
    boolean addMemberIntoMemberDetailTable(String groupName);
    boolean iniGameByPLSQL(String groupName) throws Exception;

    boolean initPredictionOfGroup(String groupName) throws Exception;

    boolean iniGroupOrder(String groupName) throws Exception;

    boolean iniOrderISO(String groupName) throws Exception;

    Result deleteGameGroupByGroupName(String groupName,HttpSession session);

    /**
     * 传入一个用户唯一标识，判断当前周期是否为年初
     * @param userUnique
     * 用户唯一标识
     * @return
     * 若当前周期为年初，返回true；否则返回false
     */
    boolean  isYearBegin(String userUnique);


    Result modifySessionStatus(HttpSession session);

}
