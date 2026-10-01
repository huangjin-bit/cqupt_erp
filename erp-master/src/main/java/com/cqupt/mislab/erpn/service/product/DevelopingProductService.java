package com.cqupt.mislab.erpn.service.product;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.product.DevelopingProduct;

import java.util.List;

/**
 * @author 张烈文
 */
public interface DevelopingProductService {


    /**
     * 根据用户唯一标识取出正在研发的产品
     *
     * @param userUnique
     * @return
     */
    Result findDevelopingProductsByUserUnique(String userUnique);


    /**
     * 添加一个产品到正在研发的表中
     *
     * @param developingProduct
     * @return
     */
    Boolean addDevelopingProduct(DevelopingProduct developingProduct);

    /**
     * 暂停研发产品
     * @param userUnique
     * @param productName
     * @return
     */
    Result updateStopDevelopProduct(String userUnique,String productName);

    /**
     * 恢复产品研发
     * @param userUnique
     * @param productName
     * @return
     */
    Result updateRecoveryDevelopingProduct(String userUnique,String productName);

//TODO 这里考虑一下是否需要加入更改研发中的产品的状态
//    /**
//     * 更改研发产品的状态
//     * @param userUnique
//     * @param productName
//     * @return
//     */
//    Boolean updateDevelopingProductStatus(String userUnique, String productName);

}
