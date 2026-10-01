package com.cqupt.mislab.erpn.service.product.Impl;

import com.cqupt.mislab.erpn.dao.product.DevelopedProductDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.DevelopedProduct;
import com.cqupt.mislab.erpn.service.product.DevelopedProductService;
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
public class DevelopedProductServiceImpl implements DevelopedProductService {

    @Autowired
    private DevelopedProductDao developedProductDao;

    @Override
    public Result findDevelopedProductsByUserUnique(String userUnique) {
        if (StringUtils.isEmpty(userUnique)) {
            log.error("【获取用户研发完成的产品错误】userUnique为空");
            return ResultUtil.error();
        }
        List<DevelopedProduct> developedProducts = null;
        try {
            developedProducts = developedProductDao.findDevelopProductByUserUnique(userUnique);
        } catch (Exception e) {
            log.error("【获取用户研发完成的产品错误】error={}", e.getMessage());
            return ResultUtil.error();
        }
        return ResultUtil.success(developedProducts);
    }
}
