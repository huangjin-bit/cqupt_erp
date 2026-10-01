package com.cqupt.mislab.erpn.dao.teacher;

import com.cqupt.mislab.erpn.dao.teacher.base.BaseDao;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupMemberStatus;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupVO;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Author xhx
 * @Date 2021/2/14 20:27
 */
@Mapper
@Repository
public interface GameGroupInfoDao extends BaseDao {


    /**
     *
     * findGameGroupMemberList 根据组名来找到所有组员的信息. 无序版
     * @param groupName
     * @return
     *List<GameGroupMemberInfo>
     * @exception
     * @since  1.0.0
     * @author hhy
     */
    List<GameGroupMemberInfo> findGameGroupMemberList(String groupName);

    /**
     * 找到所有历史的比赛信息
     * @param adminId
     * @return
     */
    List<GameGroupInfo> findHisGameGroup(String adminId);

    Integer findCurrentTime (@Param("userUnique") String userUnique);
    /**
     *
     * findAllGameGroupList 查找所有的游戏组信息.
     * @return
     *List<GameGroupInfo>
     * @exception
     * @since  1.0.0
     * @author hhy
     */
    List<GameGroupInfo> findAllGamegroup(@Param("adminID")String adminId);
    /**
     *
     * 查询所有历史游戏组的信息
     *
     * @author zhoujun
     *  添加方法参数，改为拿出该老师的所有历史分组
     * 	姚秋鸿 2019.0604
     */
    List<GameGroupInfo> findHisGameGroups(String adminID);
    /**
     *
     * findGameGroupInfoByGroupName 根据游戏组名 找到相应的游戏组信息
     * @param groupName
     * @return
     *GameGroupInfo
     * @exception
     * @since  1.0.0
     */

    GameGroupInfo findGamegroupByGroupname(String groupName);

    /**
     * 取出游戏年
     * @author lx
     * @param groupName
     * @return
     */
    int findYearByGroupname(String groupName);
    /**
     * 取出实际参赛人数
     *
     * @author yqh
     * @param groupName
     * @return
     */
    int findGamegroupMembersByGroupname(String groupName);
    /**
     *
     * findGameGroupInfoByUserUnique  根据userUnique来查找游戏组信息
     * @param userUnique
     * @return
     *GameGroupInfo
     * @exception
     * @since  1.0.0
     * @author hhy
     */
    GameGroupInfo findGamegroupByUserunique(String userUnique);

    /**
     * 根据UserUnique取出用户当前所在组的每年有多少期和当前期数(用于判断是否是年初)
     */
    GameGroupInfo findGameGroupYearAndCurrentPeriod(String userUnique);

    /**
     * 取出当前游戏组的期数
     * @param groupName
     * @return
     */
    Integer findCurrentPeriodByGroupName(@Param("groupName") String groupName);

    /**
     * 更新当前游戏组的期数
     * @author lx
     */
    boolean updateGroupCurrentPeriod(String groupName, int currentPeriod);

    /**
     *
     */
    GameGroupVO findGameGroupVO(@Param("userUnique")String userUnique, @Param("groupName")String groupName);
    /**
     *
     * @author zjs
     */
    List<GameGroupInfo> findGameGroupByAdminID(@Param("adminID") String adminID);

    List<GameGroupInfo> findGameGroupByTeacher(@Param("adminID") String adminID);

    List<GameGroupInfo> findGameGroupByUser(@Param("adminID") String adminID);


    Integer addHistory(Map<Object,Object> map);


    List<GameGroupMemberStatus> findGameGroupMemberStatusbygroupNamewithoutStartGame(String groupName);
    /***
     * 找到没有初始化完成的游戏组
     * @return
     */
    List<GameGroupInfo> findUninstalledGamegroup();
    /**找到小组成员
     * @author zhoujun
     * @param groupName
     * @return
     */
    List<GameGroupMemberStatus> findGameGroupMemberbygroupName(String groupName);

    /**
     * 查询数据库当中存储的一场比赛当中，所含有优、良、中、合格、不合格的成绩的比例
     * @param groupName
     * @return
     * @Author hyx @since 2017-07-27
     */
    GameGroupInfo selectGradeInfoByGameName(String groupName);

    /**
     * 查询数据库当中存储的一场比赛当中，所含有优、良、中、合格、不合格的成绩的比例
     * @param groupName
     * @return
     * @Author hyx @since 2017-07-28
     */
    GameGroupInfo selectGradeInfoByGameNameFromHis(String groupName);

    int setGradeProportion(GameGroupInfo gameGroupInfo);

    /**
     * 利用比赛名称去选取比赛的数据，也不知道为什么之前的学长们都没有这个方法
     * @param gameName
     * @return
     */
    GameGroupInfo selectGameGroupInfoByGameName(String gameName);

    int findMembersOfInGroupByGroupname(@Param("groupName")String groupName);

    List<GameGroupMemberDetail> findGameGroupMemberDTO(String groupName);

    GameGroupMemberDetail findMemberDetailByStudentNoAndUserUnique(@Param("userUnique") String userUnique, @Param("studentNo") String studentNo);

    void addMemberDetail(GameGroupMemberDetail groupMemberDetail);

    String findUserUniqueByUserIdAndGroupName(@Param("userId") String userId,@Param("groupName") String groupName);
}
