package com.cqupt.mislab.erpn.service.superadmin.impl;

import com.cqupt.mislab.erpn.dao.superadmin.SuperAdminDao;
import com.cqupt.mislab.erpn.model.entity.admin.AdminUser;
import com.cqupt.mislab.erpn.model.entity.superadmin.FileInfo;
import com.cqupt.mislab.erpn.model.entity.superadmin.ResultStatus;
import com.cqupt.mislab.erpn.model.entity.superadmin.SuperAdmin;
import com.cqupt.mislab.erpn.model.vo.superadmin.SuperAdminVO;
import com.cqupt.mislab.erpn.service.superadmin.SuperAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: Lunaticer
 * @Create: 2021-11-05 8:51
 * @Tip: Keeping the eyes of the prize !
 * @Description: 超级管理员Service，业务逻辑层！
 */
@Service
@Slf4j
public class SuperAdminServiceImp implements SuperAdminService {
    /**
     * FILEPATH:文件路径
     */
    private final String FILEPATH = "C:\\erpnReport\\document";

    @Autowired
    private SuperAdminDao superAdminDao;

    @Override
    public SuperAdminVO loginSuperAdmin(SuperAdmin superAdmin) {
        SuperAdmin adminByDao = superAdminDao.findInfoById(superAdmin.getSuperAdminId());
        log.info(superAdmin.getSuperAdminId());
        log.info(superAdmin.getSuperAdminName());
        log.info(superAdmin.getSuperAdminPassword());
        if (adminByDao == null) {
            return new SuperAdminVO(ResultStatus.ERROR, "该用户未注册！", null);
        } else if (!adminByDao.getSuperAdminPassword().equals(superAdmin.getSuperAdminPassword())) {
            return new SuperAdminVO(ResultStatus.ERROR, "用户名或密码输入错误！", null);
        } else {
            return new SuperAdminVO(ResultStatus.SUCCESS, "登录成功！", adminByDao);
        }
    }

    @Override
    public SuperAdminVO getAdminList() {
        List adminList = superAdminDao.findAll();
        if (null != adminList) {
            SuperAdminVO superAdminVO = new SuperAdminVO();
            superAdminVO.setCode(ResultStatus.SUCCESS);
            superAdminVO.setMsg("查询成功！");
            superAdminVO.setData(adminList);
            return superAdminVO;
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "查询失败！", null);
        }
    }

    @Override
    public SuperAdminVO changPsw(String adminId) {
        int i = superAdminDao.changePassWord(adminId);
        if (i == 1) {
            return new SuperAdminVO(ResultStatus.SUCCESS, "修改成功！", null);
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "修改失败！", null);
        }
    }

    @Override
    public SuperAdminVO removeAdminById(String adminId) {
        int i = superAdminDao.removeAdminById(adminId);

        if (i > 0) {
            return new SuperAdminVO(ResultStatus.SUCCESS, "删除成功！", null);
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "删除失败！", null);
        }
    }

    @Override
    public SuperAdminVO findAdminById(String adminId) {
        AdminUser admin = superAdminDao.findAdminById(adminId);

        if (null != admin) {
            return new SuperAdminVO(ResultStatus.SUCCESS, "查询成功！", admin);
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "查询失败！", null);
        }
    }

    @Override
    public SuperAdminVO addAdmin(AdminUser adminUser) {
        int i = superAdminDao.addAdmin(adminUser);

        List list = superAdminDao.findAll();

        if (i > 0) {
            return new SuperAdminVO(ResultStatus.SUCCESS, "添加成功！", list);
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "添加失败！", null);
        }
    }

    @Override
    public SuperAdminVO updateAdmin(AdminUser adminUser) {
        int i = superAdminDao.updateAdmin(adminUser);

        AdminUser admin = superAdminDao.findAdminById(adminUser.getAdminId());

        if (i > 0) {
            return new SuperAdminVO(ResultStatus.SUCCESS, "更新成功！", admin);
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "更新失败！", null);
        }
    }

    @Override
    public SuperAdminVO clearAll() {
        int i = superAdminDao.deleteAllDataBySuperAdmin();
        if (i > 0) {
            return new SuperAdminVO(ResultStatus.SUCCESS, "清空成功！", null);
        } else {
            return new SuperAdminVO(ResultStatus.ERROR, "清空失败！", null);
        }
    }

    @Override
    public SuperAdminVO getFileList() {
        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        FileInfo fileInfo;
        File file = new File(FILEPATH);
        //获取所有的文件和文件夹：
        File[] files = file.listFiles();
        for (File f : files) {
            int lastIndexOf = f.getName().lastIndexOf(".");
            String suffix = f.getName().substring(lastIndexOf + 1);
            fileInfo = new FileInfo(f.getName(), f.length(), longToDate(f.lastModified()), suffix);
            fileInfos.add(fileInfo);
        }
        return new SuperAdminVO(ResultStatus.SUCCESS, "查询成功！", fileInfos);
    }

    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        return sd.format(date);
    }

    @Override
    public SuperAdminVO deleteFile(String fileName) {
        File file = new File(FILEPATH + "\\" + fileName);
        file.delete();
        return new SuperAdminVO(ResultStatus.SUCCESS,"删除成功！",null);
    }
}
