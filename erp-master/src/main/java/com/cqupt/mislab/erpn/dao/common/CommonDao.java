package com.cqupt.mislab.erpn.dao.common;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/7/27 14:43
 */

@Repository
@Mapper
public interface CommonDao {

    List<String> findMemberUseruniqueByGroupName(@Param("groupName")String name);

    /**
     * 传入一个用户唯一标识，查询该用户当前处于的周期
     * @param userUnique
     * 用户唯一标识
     * @return
     * 该用户当前处于的周期
     */
    Integer findCurrentPeriod(@Param("userUnique") String userUnique);

    String findUserUniqueByUserId(@Param("userId") String userId);

    String findUserNameByUserUniqueAndUserId(@Param("userUnique") String userUnique, @Param("userId")String userId);
    String findUserNameByUserUnique(@Param("userUnique") String userUnique);

    String findGameGroupNameByUserUnique(@Param("userUnique") String userUnique);

    String findGameGroupNameByUserId(@Param("userId") String userId);

    Boolean updateRegisterUserPassword(@Param("userId") String userId, @Param("password") String password);

    Boolean updateApprovedUserPassword(@Param("userId") String userId, @Param("password") String password);

    Integer findGroupMemberSize(String userUnique);

    /**
     * 根据用户传入的UserUnique获取userId
     * @param userUnique
     * @return
     */
    String findUserIdByUserUnique(String userUnique);

    /**
     * 根据用户传入的userId获取userName
     * @param userId
     * @return
     */
    String findUserNameByUserId(String userId);

    /**
     * 根据用户传入的userId获取性别
     * @param userId
     * @return
     */
    String findGenderByUserId(String userId);


    /**
     * 查询一年的周期数
     * @param userUnique
     * @return
     */
    Integer findPeriodsOfOneYearByUserUnique(String userUnique);


    /**
     * 查询游戏总年限
     * @param userUnique
     * @return
     */
    Integer findTotalYear(String userUnique);

    /**
     * 推进下一周期执行的函数
     * @param userUnique
     * @param result
     * @return
     */
    Integer runFunctionOracle(@Param("userUnique") String userUnique, @Param("result") Integer result);
}
