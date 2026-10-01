package com.cqupt.mislab.erpn.service.advertisement.impl;

import com.cqupt.mislab.erpn.dao.market.MarketDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementDao;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AdvertisementServiceImplTest {


    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private MarketDao marketDao;

    @Autowired
    private AdvertisementDao advertisementDao;
    @Test
    public void findDevelopedMarketByUserUnique() {
        System.out.println(advertisementService.findDevelopedMarketByUserUnique("qw161216091758794").toString());
        System.out.println(marketDao.getMarketsByUserUnique("qw161216091758794"));
        System.out.println(advertisementDao.findAdvertisementByMarketNameAndUserUnique("本地市场", "qw161216091758794"));

    }

    @Test
    public void name() {
        advertisementService.updateAdvertisementFinish("qw161216091758794");
    }
}