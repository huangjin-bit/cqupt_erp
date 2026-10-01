package com.cqupt.mislab.erpn.service.product.Impl;

import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.dao.material.MaterialBasicDao;
import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.material.MaterialBasic;
import com.cqupt.mislab.erpn.model.entity.product.ProductDetailBasic;
import com.cqupt.mislab.erpn.model.entity.product.ProductOfUser;
import com.cqupt.mislab.erpn.service.product.ProductBasicService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 张烈文
 */
@Slf4j
@Service
public class ProductBasicServiceImpl implements ProductBasicService {

    @Autowired
    private ProductBasicDao productBasicDao;

    @Autowired
    private MaterialBasicDao materialBasicDao;
    @Override
    public Result findProductInventories(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("【获取用户产品库存信息错误】userUnique={}", userUnique);
            return ResultUtil.error();
        }
        List<ProductOfUser> productOfUsers=null;
        try {
            productOfUsers = productBasicDao.findAllProductInventoriesByUserUnique(userUnique);
        } catch (Exception e) {
            log.error("【获取用户产品库存信息错误】error={}", e.getMessage());
            return ResultUtil.error();
        }
        return ResultUtil.success(productOfUsers);
    }

    @Override
    public Result findProductName() {
        List<String> productNames = productBasicDao.findAllProductName();
        return ResultUtil.success(productNames);
    }

    @Override
    public Result findProductDetail() {
        List<ProductDetailBasic> productDetailBasics = productBasicDao.findProductDetail();
        return ResultUtil.success(productDetailBasics);
    }

    @Override
    public Float findProductPrice(String productName) {
        float A = 0;
        List<String> materialNames = productBasicDao.findMaterialNameByProductName(productName);
        for (String name : materialNames) {
            int needNumber = productBasicDao.findMaterialNumber(productName, name);
            MaterialBasic materialBasicByName = materialBasicDao.getMaterialBasicByName(name);
            Integer price = materialBasicByName.getPrice();
            //lx:四舍五入处理
            A += ERPUtils.round(price * needNumber);    //会计分录里面的A值
        }
        return A;
    }


    @Override
    public boolean updateDecreasePNumber(ProductOfUser productOfUser) {
        return false;
    }
}
