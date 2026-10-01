package com.cqupt.mislab.erpn.dao.group;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.po.TeacherPo;
import com.cqupt.mislab.erpn.model.vo.advertisement.AdOfStatusVO;
import com.cqupt.mislab.erpn.model.vo.gamegroup.GameGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/8/10 8:51
 */

@Repository
@Mapper
public interface GroupManageDao {

    //老师建立的分组
    List<GameGroupInfo> findGameGroupInfoByTeacher(@Param("adminID") String adminID);
    //用户自建立的分组
    List<GameGroupInfo> findGameGroupByUser(@Param("adminID") String adminID);
    //模糊搜索
    List<GameGroupInfo> findGameGroupByUserVague(@Param("adminID") String adminID, @Param("vagueName")String vagueName);

    List<GameGroupInfo> findGameGroupByTeacherVague(@Param("adminID") String adminID, @Param("vagueName")String vagueName);

    /**
     * 在gamegroup表中新增一个游戏分组
     * @param groupName
     * @param groupCreatorId
     * @param teacherId
     * @param userNumbers
     * @param years
     * @param periodsOfOneYear
     * @return boolean
     */
    Boolean addGroup(@Param("groupName") String groupName,
                     @Param("groupCreatorId") String groupCreatorId,
                     @Param("teacherId") String teacherId,
                     @Param("userNumbers") int userNumbers,
                     @Param("years") int years,
                     @Param("periodsOfOneYear") int periodsOfOneYear
                     );

    /**
     * 在gamegroupmember表中新增一个学生与组的对应关系
     * @param groupName
     * @param userId
     * @return boolean
     */
    Boolean addGroupMember(@Param("groupName") String groupName,@Param("userId") String userId);

    Boolean deleteGroupMember(@Param("groupName") String groupName,@Param("userId") String userId);

    GameGroupInfo findGroupByGroupName(@Param("groupName") String groupName);

    //TODO
    GameGroupMemberInfo findGroupNameByUserId(@Param("userId") String userId);

    List<TeacherPo> getTeacherList();

    List<String> getTeacherIdList();

    List<GameGroupInfo> getGroupList();

    void iniGameByPLSQL(Map<String, Object> map);

    Integer findYearsByGroupName(String groupName);

    Boolean updateGroupCurrentPeriod(@Param("groupName") String groupName,@Param("currentPeriod") int currentPeriod);

    /**
     * 根据组名删除分组
     * @param groupName
     * @return
     */
    Boolean deleteGameGroupByGroupName(String groupName);

    /**
     * 根据组名删除所有组员
     * @param groupName
     * @return
     */
    Boolean deleteGroupMembersByGroupName(String groupName);

    /**
     * 获取小组中还未破产的所有小组成员
     * @param userStatus
     * @param userUnique
     * @return
     */
    List<GameGroupMemberInfo> findGameGroupMemberListByStatus(@Param("userStatus") Integer userStatus, @Param("userUnique") String userUnique);

   String findUserNameByUserId(String userId);


    /**
     * 根据userUnique来查找游戏组信息
     * @param userUnique
     * @return
     */
    GameGroupInfo findGameGroupInfoByUserUnique(String userUnique);

    /**
     * 更新破产用户的状态
     * @param userUnique
     * 用户唯一标识符
     * @param status
     * 破产用户状态为0
     * @return
     * true-更新成功；
     * false-更新失败
     */
    Boolean updateBankruptcyUserStatus(@Param("userUnique") String userUnique, @Param("status") int status);

    AdOfStatusVO findAdOfStatusByUseruniqueAndPeriod(@Param("userUnique") String userUnique, @Param("period")String period);
    int findStatusByUserUnique(String userUnique);

    GameGroupInfo findGameGroupYearAndCurrentPeriod(String userUnique);

    GameGroupVo findGameGroupVo(@Param("userUnique") String userUnique,@Param("groupName") String groupName);

    void updateIncreaseUserCurrentPeriod(String userUnique);

    void updateSetUserCurrentPeriod(@Param("userUnique") String userUnique, @Param("period") int period);

    int findLeastCurrentPeriodByGroupName(String groupName);

    /**
     * 查询当前小组进行到的周期
     * @param groupName
     * @return
     */
    int findCurrentPeriodByGroupName(String groupName);

    /**
     * 用户已经完成游戏，设置他的状态值为2
     * @param userUnique
     */
    void updateUserStatusToFinishGame(String userUnique);

    /**
     * 查询数据库当中存储的一场比赛当中，所含有优、良、中、合格、不合格的成绩的比例
     *
     * @param groupName
     * @return
     * @Author hyx @since 2017-07-27
     */
    GameGroupInfo selectGradeInfoByGameName(String groupName);

    /**
     * 利用比赛名称去选取比赛的数据，
     *
     * @param groupName
     * @return
     */
    GameGroupInfo selectGameInfoByGameName(String groupName);

    /**
     * 根据组名查出所有组员信息
     * @param gameGroup
     * @return
     */
    List<GameGroupMemberInfo> findGroupMemberByGameGroup(String gameGroup);

    /**
     * 根据userUnique查出组长名
     * @param userUnique
     * @return
     */
    String findGroupMemberByUserUnique(String userUnique);

}
