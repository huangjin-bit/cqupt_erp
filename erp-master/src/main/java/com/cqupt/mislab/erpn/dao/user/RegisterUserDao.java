package com.cqupt.mislab.erpn.dao.user;

import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2020/7/20 10:10
 */

@Repository
@Mapper
public interface RegisterUserDao {

    Boolean addRegisterUser(@Param("userId") String userId, @Param("userName") String userName, @Param("password") String password, @Param("major") String major, @Param("sClass") String sClass, @Param("sId") String sId, @Param("email") String email, @Param("tel") String tel, @Param("gender") String gender);

    RegisterUser findRegister(@Param("userId")String userId);

    Boolean deleteRegisterByUserId(@RequestParam("userId")String userId);

    Boolean modifyRegisterUser(@Param("userId") String userId, @Param("userName") String userName, @Param("password") String password, @Param("major") String major, @Param("sClass") String sClass, @Param("sId") String sId, @Param("email") String email, @Param("tel") String tel, @Param("gender") String gender);

}
