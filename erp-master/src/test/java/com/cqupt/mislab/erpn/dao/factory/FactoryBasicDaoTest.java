package com.cqupt.mislab.erpn.dao.factory;

import com.cqupt.mislab.erpn.model.entity.factory.FactoryBasic;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FactoryBasicDaoTest {

    @Autowired
    private FactoryBasicDao factoryBasicDao;
    @Test
    public void getFactoryBasic() {
        FactoryBasic factoryBasic = factoryBasicDao.getFactoryBasic("大厂房");

    }
}