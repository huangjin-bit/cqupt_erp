package com.cqupt.mislab.erpn.service.user;

import com.cqupt.mislab.erpn.commons.constant.LoginResponse;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.UserStudentInfoRegisterDto;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2020/7/20 10:11
 */
public interface UserService {
    /**
     *
     * 添加注册
     *
     * @param studentInfoRegisterDto
     * @return
     */
//    /**
//     * 添加注册
//     * @param request
//     * @param userId
//     * @param userName
//     * @param sId
//     * @param major
//     * @param sClass
//     * @param email
//     * @param tel
//     * @param passsword
//     * @return
//     */
//    Result addRegisterUser(HttpServletRequest request, @RequestParam("userId")String userId, @RequestParam("userName")String userName, @RequestParam("sId")String sId, @RequestParam("major")String major, @RequestParam("sClass")String sClass, @RequestParam("email")String email, @RequestParam("tel")String tel, @RequestParam("passsword")String passsword);
    Result addRegisterUser(UserStudentInfoRegisterDto studentInfoRegisterDto);

    Result findRegister(@Param("userId")String userId);

    Result deleteRegisterByUserId(@Param("userId")String userId);

    Result addApprovedUser(RegisterUser registerUser);

    Result findRegisterByUserId(@Param("userId")String userId);

    Result findApprovedUserByUserId(@Param("userId")String userId);

    int isExistApprovedUser(@Param("userId") String userId, @Param("password") String password);

    int isExistRegister(@Param("userId") String userId, @Param("password") String password);

    Boolean isExistGameGroup(@Param("groupName")String groupName);

    Boolean isGroupMember(@Param("userId") String userId);

    Boolean isGameRunning(@Param("userId") String userId);

    Boolean isGameCreator(@Param("userId") String userId);

    Result updatePassword(@Param("userId")String userId,@Param("password") String password);

    int login(@Param("userId") String userId, @Param("password") String password,HttpServletRequest request, HttpServletResponse response);

    //TODO: 添加-1000code，判断是否授权  2020.7.29王永、刘岽、任崇伟、孙鹏翔讨论后面处理。
    LoginResponse loginResponse(@Param("loginStatus")int loginStatus,@Param("userId") String userId, HttpServletRequest request, HttpServletResponse response);

    Result modifyUser(UserStudentInfoRegisterDto studentInfoRegisterDto);

    int loginStatus(String userId);

    Result logout(HttpServletRequest request, HttpServletResponse response);

    /**
     * 开始比赛后，获取用户比赛的相关信息
     * 用户相关信息，现金，厂房，税金，分组
     * @param userUnique
     * @return
     */
    Result getUserInfo(String userUnique);

    /**
     * 重新开始游戏
     * @param userUnique
     * 用户唯一标识符
     * @return
     * 0 - 成功；
     * 1 - 失败
     */
    Result replay(String userUnique);

}
