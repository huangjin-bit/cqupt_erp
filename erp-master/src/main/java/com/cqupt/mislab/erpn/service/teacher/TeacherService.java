package com.cqupt.mislab.erpn.service.teacher;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.dto.teacher.ImportResultRow;
import com.cqupt.mislab.erpn.model.dto.teacher.GameImportRow;
import com.cqupt.mislab.erpn.model.dto.teacher.SystemResetSummary;
import com.cqupt.mislab.erpn.model.dto.teacher.StudentImportRow;
import com.cqupt.mislab.erpn.model.entity.user.BasicUser;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.service.impl
 * @description:
 * @author: keji0
 * @date: 2020/7/28 0:59
 */


public interface TeacherService {

    Boolean importStudents(List<ApprovedUser> infos,String adminId);

    List<ImportResultRow> importStudentsDetailed(List<StudentImportRow> rows, String adminId);

    Boolean deleteBatchUsers(String[] ids) throws Exception;

    Boolean deleteApprovedUser(String userId) throws Exception;

    List<BasicUser> rangeSearch(String keyword,int status);

    BasicUser exactSearch(String UserUnique,int status);

    boolean resetPassword(int status,String userId);

    Boolean teacherLogin(String teacherId, String password, HttpSession session);

    List<ApprovedUser> pageUser(int page,int limit,int status);

    List getAllUser(int status);

//    List<RegisterUser> pageRegisterUser(int page,int limit);

    Integer countRecordsApprovedUser(int limit);

    Integer countRecordsRegisterUser(int limit);

    Result findAllRegister();

    Result findAllApprovedUser();

    Result passRegisterUser(@Param("userId")String userId, @Param("adminId")String adminId);

    Result refuseRegisterUser(@Param("userId")String userId,@Param("adminId")String adminId);

    Result passBatchRegisterUsers(@Param("userIds") String[] userIds,@Param("adminId")String adminId);

    Result refuseBatchRegisterUsers(@Param("userIds") String[] userIds,@Param("adminId")String adminId);

    Result importGameStudents(List<String> gameUsers, String adminId);

    Result importGameStudentsDetailed(List<GameImportRow> rows, String adminId);

    SystemResetSummary resetSystemData();
}




