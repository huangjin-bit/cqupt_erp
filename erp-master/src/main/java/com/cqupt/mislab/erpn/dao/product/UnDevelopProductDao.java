package com.cqupt.mislab.erpn.dao.product;

import com.cqupt.mislab.erpn.model.entity.product.UnDevelopProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 */
@Repository
@Mapper
public interface UnDevelopProductDao {
    /**
     * 取出还未开始研发的产品
     * @param userUnique
     * @return
     */
    List<UnDevelopProduct> findUnDevelopProductsByUserUnique(String userUnique);


    /**
     * 根据userUnique和productName取出用户某个特定的还未开始研发的产品
     * @param userUnique
     * @param productName
     * @return
     */
    UnDevelopProduct findUnDevelopProductByUserUnique(String userUnique,String productName);

    /**
     * 删除某个未研发的产品（该产品进入研发期）
     * @param userUnique
     * @param productName
     * @return
     */
    Boolean deleteUnDevelopProduct(String userUnique,String productName);
}
