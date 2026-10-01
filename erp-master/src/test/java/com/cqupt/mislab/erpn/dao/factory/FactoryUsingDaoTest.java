package com.cqupt.mislab.erpn.dao.factory;

import com.cqupt.mislab.erpn.model.entity.factory.FactoryUsing;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class FactoryUsingDaoTest {
    @Autowired
    private FactoryUsingDao factoryUsingDao;

    @Test
    public void test() {
        List<FactoryUsing> factoryUsings = factoryUsingDao.listFactoryUsingsByPlace("ray162609356272813", "本地市场");
        Assert.assertTrue(factoryUsings.size() > 0);
    }

}