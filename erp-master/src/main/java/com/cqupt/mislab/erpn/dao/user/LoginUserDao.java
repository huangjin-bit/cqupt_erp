package com.cqupt.mislab.erpn.dao.user;

import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import com.cqupt.mislab.erpn.model.po.UserPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/7/25 16:02
 */

@Repository
@Mapper
public interface LoginUserDao {
    RegisterUser findRegisterByUserId(@Param("userId")String userId);

    ApprovedUser findApprovedUserByUserId(@Param("userId")String userId);

    GameGroupMemberInfo findGameGroupMemberByUserId(@Param("userId")String userId);

    GameGroupMemberInfo findUserUniqueInGroupMemberByUserId(@Param("userId")String userId);

    List<GameGroupMemberInfo> findGroupMembersByGroupName(@Param("groupName") String groupName);

    GameGroupInfo findGameGroupByGroupName(@Param("groupName") String groupName);

    int findGameCreatorByUserId(@Param("userId")String userId);

    /**
     * 通过给定的用户唯一标识，查询该用户的状态
     * @param userUnique
     * 用户唯一标识
     * @return
     * 初始时status为null；
     * 0表示用户已经破产，但可以写报告；
     * 1表示用户仍然在经营；
     * 2表示，用户已经完成了竞赛中的所有周期；
     * -1表示用户结束经营，退出了比赛，对该组的比赛将不再产生影响；
     */
    int getStatusByUserUnique(@Param("userUnique")String userUnique);

    Boolean addApprovedUser(RegisterUser registerUser);

    Boolean deleteApprovedUser(@Param("userId")String userId);

    Boolean modifyApprovedUser(@Param("userId") String userId, @Param("userName") String userName, @Param("password") String password, @Param("major") String major, @Param("sClass") String sClass, @Param("sId") String sId, @Param("email") String email, @Param("tel") String tel, @Param("gender") String gender);

    UserPo loginRegisterUser(@Param("userId")String userId);

    UserPo loginApprovedUser(@Param("userId")String userId);

    /**
     * 根据组名（groupName）从审批用户表（approveduser）中获取组内的审批用户（ApprovedUser）的集合
     * @param groupName
     * @return
     */
    List<ApprovedUser> findGroupUserMessageByGroupName(@Param("groupName")String groupName);

    ApprovedUser findGroupCreatorUserByGroupName(@Param("groupName")String groupName);

    Boolean updateGameGroupMember(GameGroupMemberInfo gameGroupMemberInfo);

}
