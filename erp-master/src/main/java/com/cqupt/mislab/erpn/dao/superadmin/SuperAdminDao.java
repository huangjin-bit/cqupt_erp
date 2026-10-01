package com.cqupt.mislab.erpn.dao.superadmin;

import com.cqupt.mislab.erpn.model.entity.admin.AdminUser;
import com.cqupt.mislab.erpn.model.entity.superadmin.SuperAdmin;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SuperAdminDao {

    /**
     * 根据超级管理员用户Id查询数据
     * @param superAdminId
     * @return SuperAdmin
     **/
    SuperAdmin findInfoById(String superAdminId);

    /**
     * 查询所有管理员
     * @return List：管理员列表
     */
    List findAll();

    /**
     * 改变管理员密码
     * @param adminId
     * @return int
     */
    int changePassWord(String adminId);

    /**
     * 删除管理员
     * @param adminId
     * @return int
     */
    int removeAdminById(String adminId);

    /**
     * 根据管理员ID查询管理员记录
     * @param adminId
     * @return AdminUser：管理员
     */
    AdminUser findAdminById(String adminId);

    /**
     * 添加管理员记录
     * @param adminUser
     * @return int
     */
    int addAdmin(AdminUser adminUser);

    /**
     * 修改管理员记录
     * @param adminUser
     * @return int
     */
    int updateAdmin(AdminUser adminUser);

    /**
     * 清空数据库
     * @return
     */
    int deleteAllDataBySuperAdmin();
}
