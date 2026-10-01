package com.cqupt.mislab.erpn.service.product.Impl;


import com.cqupt.mislab.erpn.dao.product.DevelopingProductDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.DevelopingProduct;
import com.cqupt.mislab.erpn.service.product.DevelopingProductService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author 张烈文
 */
@Service
@Slf4j
public class DevelopingProductServiceImpl implements DevelopingProductService {

    @Autowired
    private DevelopingProductDao developingProductDao;

    @Override
    public Result findDevelopingProductsByUserUnique(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("【取出正在研发的产品错误】userUnique不能为空");
            return ResultUtil.error();
        }
        List<DevelopingProduct> developingProducts =null;
        try {
            developingProducts = developingProductDao.findDevelopingProductsByUserUnique(userUnique);
        } catch (Exception e) {
            log.error("【取出正在研发的产品错误】error={}",e.getMessage());
            return ResultUtil.error();
        }
        return ResultUtil.success(developingProducts);
    }

    @Override
    public Boolean addDevelopingProduct(DevelopingProduct developingProduct) {
        if (developingProduct == null) {
            log.error("【添加产品到正在研发表中错误】developingProduct=null");
            return false;
        }
        try {
            developingProductDao.addDevelopingProduct(developingProduct);
        } catch (Exception e) {
            log.error("【添加产品到正在研发的表中错误】error={}", e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public Result updateStopDevelopProduct(String userUnique,String productName) {
        if (StringUtils.isEmpty(userUnique)||StringUtils.isEmpty(productName)) {
            log.error("【暂停研发错误】userUnique={},productName={}",userUnique,productName);
            return ResultUtil.error();
        }
        try {
            developingProductDao.updateStopDevelopProduct(userUnique, productName);
        } catch (Exception e) {
            log.error("【暂停研发产品错误】error={}", e.getMessage());
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

    @Override
    public Result updateRecoveryDevelopingProduct(String userUnique,String productName) {
        if (StringUtils.isEmpty(userUnique)||StringUtils.isEmpty(productName)) {
            log.error("【恢复研发错误】userUnique={},productName={}",userUnique,productName);
            return ResultUtil.error();
        }
        try {
            developingProductDao.updateRecoveryDevelopProduct(userUnique, productName);
        } catch (Exception e) {
            log.error("【恢复研发产品错误】error={}", e.getMessage());
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }

}
