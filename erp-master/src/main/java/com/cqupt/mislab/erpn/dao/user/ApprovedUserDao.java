package com.cqupt.mislab.erpn.dao.user;

import com.cqupt.mislab.erpn.dao.teacher.base.BaseDao;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/9/12 21:22
 */
@Repository
@Mapper
public interface ApprovedUserDao extends BaseDao {

    RegisterUser findByUserid(@Param("userID")String userId);

    RegisterUser findByName(@Param("name")String name);

    RegisterUser findByStudentId(@Param("studentID")String studentId);

    RegisterUser findByClassName(@Param("className")String className);

    RegisterUser findByMajor(@Param("major")String major);

    void updateApproveduser(RegisterUser approvedUserInfo);

    RegisterUser findAllApproveduser();

    RegisterUser findGamegroupcreatorByGroupname(@Param("groupName")String groupName);

    List<RegisterUser> findAllGamegroupuserByGroupname(@Param("groupName")String groupName);
}
