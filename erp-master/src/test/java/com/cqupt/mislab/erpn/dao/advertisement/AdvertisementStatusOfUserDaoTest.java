package com.cqupt.mislab.erpn.dao.advertisement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdvertisementStatusOfUserDaoTest {

    @Autowired
    private AdvertisementStatusOfUserDao advertisementStatusOfUserDao;
    @Test
    public void updateChooseOrderFlag() {
        String[] userUniques = {"rr161380305455473", "sw161224282780227"};
        advertisementStatusOfUserDao.updateChooseOrderFlag(userUniques, 1, 2);

    }
}