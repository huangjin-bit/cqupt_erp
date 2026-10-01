package com.cqupt.mislab.erpn.service.product;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 */
public interface DevelopedProductService {


    /**
     * 取出用户研发完成的产品
     * @param userUnique
     * @return
     */
    Result findDevelopedProductsByUserUnique(String userUnique);

}
