package com.cqupt.mislab.erpn.dao.teacher;

import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.entity.user.BasicUser;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import com.cqupt.mislab.erpn.model.po.TeacherPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/7/28 1:04
 */

@Repository
@Mapper
public interface TeacherDao {

    Boolean importApprovedUser(@Param("users") List<ApprovedUser> users);

    Boolean deleteReference(@Param("userId")String userId);

    List<BasicUser> findUsersUnclear(@Param("status")int status,@Param("keyword")String userId);

    BasicUser findBasicUser(@Param("status")int status,@Param("userId")String userId);

    Boolean resetPasswordRegister(@Param("userId")String userId);

    Boolean resetPasswordApproved(@Param("userId")String userId);

    List<RegisterUser> findAllRegister();

    List<ApprovedUser> findAllApprovedUser();

    HashSet<String> findAllApprovedUserByAdminId(@Param("adminId")String adminId);

    List<ApprovedUser> findApprovedUsersByGameUsers(@Param("gameUsers") List<String> gameUsers);

    List<ApprovedUser> pageFindApprovedUser(@Param("start")int start,@Param("limit")int limit);

    List<RegisterUser> pageFindRegisterUser(@Param("start")int start,@Param("limit")int limit);

    Boolean insertReferences(@Param("stuId") String stuId,@Param("teacherId")String teacherId);

    TeacherPo judgeAccount(String teacherId, String password);

    int countRecordsApprovedUser();

    int countRecordsRegisterUser();

    void batchInsertTeacherReferences(@Param("users") List<ApprovedUser> users, @Param("teacherId") String teacherId);

    int countTableRows(@Param("tableName") String tableName);

    int deleteAllTableRows(@Param("tableName") String tableName);
}



