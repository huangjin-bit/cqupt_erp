package com.cqupt.mislab.erpn.controller.superadmin;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.LoginForm;
import com.cqupt.mislab.erpn.model.entity.admin.AdminUser;
import com.cqupt.mislab.erpn.model.entity.superadmin.AddAdmin;
import com.cqupt.mislab.erpn.model.entity.superadmin.ResultStatus;
import com.cqupt.mislab.erpn.model.entity.superadmin.SuperAdmin;
import com.cqupt.mislab.erpn.model.vo.superadmin.SuperAdminVO;
import com.cqupt.mislab.erpn.service.superadmin.impl.SuperAdminServiceImp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Author: Lunaticer
 * @Create: 2021-11-05 9:06
 * @Tip: Keeping the eyes of the prize !
 * @Description: 超级管理员Controller
 */
@CrossOrigin
@Api("超级管理员")
@RestController
@RequestMapping("/superadmin")
@Slf4j
public class SuperAdminController {

    @Autowired
    private SuperAdminServiceImp superAdminServiceImp;

    @ApiOperation("登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public SuperAdminVO login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
        String superAdminId = loginForm.getUserId();
        String pwd = loginForm.getPassword();
        SuperAdmin superAdmin = new SuperAdmin(superAdminId, "", pwd);
        SuperAdminVO superAdminVO = superAdminServiceImp.loginSuperAdmin(superAdmin);
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo == null) {
            session.setAttribute("superAdmin", superAdminVO.getData());
        }
        return superAdminVO;
    }

    @ApiOperation("超级管理员账号登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.invalidate();
        return ResultUtil.success("登出成功");
    }

    @ApiOperation("添加管理员")
    @PostMapping("/adminadd")
    public SuperAdminVO addAdmin(@RequestBody AddAdmin adminUser, HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        String adminId = adminUser.getAdminId();
        String adminName = adminUser.getAdminName();
        String adminPwd = adminUser.getAdminPwd();

        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo != null) {
            AdminUser addAdminUser = new AdminUser(adminId, adminName, adminPwd, "teacher", null, null, null, null);
            superAdminVO = superAdminServiceImp.addAdmin(addAdminUser);
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }

    @ApiOperation("获取管理员列表")
    @RequestMapping(value = "/adminlist", method = RequestMethod.GET)
    public SuperAdminVO getAdminList(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo == null) {
            return new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        } else {
            SuperAdminVO adminList = superAdminServiceImp.getAdminList();
            return adminList;
        }
    }

    @ApiOperation("删除管理员")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public SuperAdminVO deleteAdmin(@RequestParam("adminId") String adminId, HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo != null) {
            superAdminVO = superAdminServiceImp.removeAdminById(adminId);
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }

    @ApiOperation("查询管理员")
    @RequestMapping(value = "/getadmin", method = RequestMethod.GET)
    public SuperAdminVO findAdminById(@RequestParam("adminId") String adminId, HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo != null) {
            superAdminVO = superAdminServiceImp.findAdminById(adminId);
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }

    @ApiOperation("重置管理员密码")
    @RequestMapping(value = "/setpwd", method = RequestMethod.GET)
    public SuperAdminVO resetPwd(@RequestParam("adminId") String adminId, HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");

        if (superAdminInfo != null) {
            superAdminVO = superAdminServiceImp.changPsw(adminId);
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }

    @ApiOperation("清空所有数据")
    @RequestMapping(value = "/clearall", method = RequestMethod.GET)
    public SuperAdminVO clearAll(HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo != null) {
            superAdminVO = superAdminServiceImp.clearAll();
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }

    @ApiOperation("获取文件列表")
    @RequestMapping(value = "/filelist", method = RequestMethod.GET)
    public SuperAdminVO getFileList(HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo != null) {
            superAdminVO = superAdminServiceImp.getFileList();
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }

    @ApiOperation("删除文件")
    @RequestMapping(value = "/deleteFile", method = RequestMethod.GET)
    public SuperAdminVO deleteFile(@RequestParam("fileName") String fileName, HttpServletRequest request) {
        SuperAdminVO superAdminVO;
        HttpSession session = request.getSession();
        SuperAdmin superAdminInfo = (SuperAdmin) session.getAttribute("superAdmin");
        if (superAdminInfo != null) {
            superAdminVO = superAdminServiceImp.deleteFile(fileName);
        } else {
            superAdminVO = new SuperAdminVO(ResultStatus.ERROR, "请先登录！", null);
        }
        return superAdminVO;
    }


}