package com.cqupt.mislab.erpn.dao;


import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductBasicDaoTest {

    @Autowired
    private ProductBasicDao productBasicDao;

    @Test
    public void selectAllProductBasic() {
        List<String> productBasics = productBasicDao.findAllProductName();
        Assert.assertNotEquals(0, productBasics.size());
        System.out.println(productBasics.get(0));

    }

    @Test
    public void findAllProductName() {
    }

    @Test
    public void findAllProductInventoriesByUserUnique() {
    }

    @Test
    public void findProductDetail() {
    }

    @Test
    public void findMaterialNameByProduct() {
    }

    @Test
    public void findMaterialNumber() {
    }

    @Test
    public void findPNumberByUserUniqueAndProductName() {
    }

    @Test
    public void updateProductOfUserPNumber() {
    }
}