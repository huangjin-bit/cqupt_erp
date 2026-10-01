package com.cqupt.mislab.erpn.service.productline.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.productline.ProductLineBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLineBasic;
import com.cqupt.mislab.erpn.service.product.ProductBasicService;
import com.cqupt.mislab.erpn.service.productline.ProductLineBasicService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/15 18:03
 */
@Service
@Slf4j
public class ProductLineBasicServiceImpl implements ProductLineBasicService {
    @Autowired
    private ProductLineBasicDao productLineBasicDao;

    @Override
    public Result listProductLineBasic() {
        return ResultUtil.success(productLineBasicDao.listProductLineBasic());
    }
}
