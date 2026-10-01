package com.cqupt.mislab.erpn.dao.product;

import com.cqupt.mislab.erpn.model.entity.product.DevelopedProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 *
 */
@Repository
@Mapper
public interface DevelopedProductDao {


   //TODO 是否需要查询已经研发了的产品

    /**
     * 取出用户已研发完成的产品
     * @param userUnique
     * @return
     */
    List<DevelopedProduct> findDevelopProductByUserUnique(String userUnique);
}
