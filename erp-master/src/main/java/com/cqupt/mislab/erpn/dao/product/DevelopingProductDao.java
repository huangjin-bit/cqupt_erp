package com.cqupt.mislab.erpn.dao.product;

import com.cqupt.mislab.erpn.model.entity.product.DevelopingProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 */
@Repository
@Mapper
public interface DevelopingProductDao {

    /**
     * 取出研发中的产品
     *
     * @param userUnique
     * @return
     */
    List<DevelopingProduct> findDevelopingProductsByUserUnique(String userUnique);

    /**
     * 添加一个研发的产品到研发表中
     * @param developingProduct
     * @return
     */
    Boolean addDevelopingProduct(DevelopingProduct developingProduct);

    /**
     * 暂停产品的研发
     * @param userUnique
     * @param productName
     * @return
     */
    Boolean updateStopDevelopProduct(String userUnique,String productName);

    /**
     * 恢复产品的研发
     * @param userUnique
     * @param productName
     * @return
     */
    Boolean updateRecoveryDevelopProduct(String userUnique,String productName);


    /**
     * 通过用户唯一标识和产品名找到用户正在研发的产品
     * @param userUnique
     * @param productName
     * @return
     */
    DevelopingProduct findDevelopingProduct(String userUnique, String productName);
}
