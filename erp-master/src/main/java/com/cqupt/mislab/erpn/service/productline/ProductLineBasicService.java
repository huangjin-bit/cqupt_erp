package com.cqupt.mislab.erpn.service.productline;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLineBasic;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/15 17:58
 */
public interface ProductLineBasicService {


    /**
     * 获取所有的生产线信息
     *
     * @return
     */
    Result listProductLineBasic();

}
