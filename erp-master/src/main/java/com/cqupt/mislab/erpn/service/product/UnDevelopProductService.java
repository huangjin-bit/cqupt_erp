package com.cqupt.mislab.erpn.service.product;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.UnDevelopProduct;

import java.util.List;

/**
 * @author 张烈文
 */
public interface UnDevelopProductService {


    /**
     * 研发新产品
     * @param userUnique
     * @param productName
     * @return
     */
    Result updateDevelopProduct(String userUnique, String productName);

    /**
     * 根据用户标识，取出还未研发的产品
     * @param userUnique
     * @return
     */
    Result findUnDevelopProductByUserUnique(String userUnique);


    /**
     * 根据用户唯一标识和产品名取出还未开始研发的产品
     * @param userUnique
     * @param productName
     * @return
     */
    Result findUnDevelopByUserUnique(String userUnique,String productName);

    /**
     * 删除某个未研发的产品(该产品进入研发期)
     * @param userUnique
     * @param productName
     * @return
     */
    Boolean deleteUnDevelopProduct(String userUnique,String productName);
}
