package com.cqupt.mislab.erpn.service.advertisement;

import io.swagger.annotations.ApiOperation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AdvertisementServiceTest {

    @Autowired
    private AdvertisementService advertisementService;


    @Test
    public void findAdvertisementByMarketNameAndUserUnique() {
    }
}