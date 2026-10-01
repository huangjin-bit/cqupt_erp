package com.cqupt.mislab.erpn.service.user.impl;

import com.cqupt.mislab.erpn.ErpnApplication;
import com.cqupt.mislab.erpn.ErpnApplicationTests;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementStatusOfUserService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

@Slf4j
public class UserServiceImplTest  extends ErpnApplicationTests {
    @Autowired
    private UserService userService;

    @Autowired
    private CommonDao commonDao;
    @Autowired
    private AdAndOrderRefreshService adAndOrderRefreshService;
    @Autowired
    private AdvertisementStatusOfUserService service;
    @Autowired
    private GroupManageService groupManageService;
    @Test
    public void getUserInfo() {
        Result info = userService.getUserInfo("ray162124270639707");
        log.info("info={}", info);
    }

    @Test
    public void getUserName() {
        String unique = commonDao.findUserIdByUserUnique("aqiuya161941918771138");
        String userNaem = commonDao.findUserNameByUserId(unique);
        adAndOrderRefreshService.advertiseComplete("aqiuya161941918771138");
        System.out.println(unique);
        System.out.println(userNaem);

    }

    @Test
    public void getUserInfo1() {
        service.findMembersCurProgress("ray162245039194866");
    }

    @Test
    public void getTeacherUsers() {
        Result teacherList = groupManageService.getTeacherList();
        log.info("result={}", teacherList);
    }
}