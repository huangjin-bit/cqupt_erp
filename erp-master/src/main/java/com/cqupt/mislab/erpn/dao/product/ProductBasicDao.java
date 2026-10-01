package com.cqupt.mislab.erpn.dao.product;

import com.cqupt.mislab.erpn.model.entity.product.ProductBasic;
import com.cqupt.mislab.erpn.model.entity.product.ProductDetailBasic;
import com.cqupt.mislab.erpn.model.entity.product.ProductOfUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 */
@Mapper
@Repository
public interface ProductBasicDao {
    /**
     * 查询所有产品的名称
     * @return
     */
    List<String> findAllProductName();


    /**
     * 显示所有产品的库存信息
     * @param userUnique
     * @return
     */
    List<ProductOfUser> findAllProductInventoriesByUserUnique(String userUnique);

    /**
     * 查看产品详情,包含产品研发所需要的材料及数量
     * @return
     */
    List<ProductDetailBasic> findProductDetail();

    /**
     * 查看产品所需原材料种类
     * @param productName
     * @return
     */
    List<String> findMaterialNameByProductName(String productName);

    /**
     * 查询出生产该产品所需要的原材料数量
     * @param productName
     * @param materialName
     * @return
     */
    int findMaterialNumber(String productName, String materialName);

    /**
     * 查数询用户某个产品的库存量
     * @param productOfUser
     * @return
     */
    int findPNumberByUserUniqueAndProductName(ProductOfUser productOfUser);


    /**
     * 根据用户唯一标识和产品名称更新用户对应产品的库存数量
     * @param productOfUser
     * @return
     */
    Boolean updateProductOfUserPNumber(ProductOfUser productOfUser);

    /**
     * 减少用户的指定产品的库存
     * @param userUnique
     * @param orderPNumber
     * @param productName
     */
    int updateDecreasePNumber(@Param("userUnique") String userUnique, @Param("orderPNumber") int orderPNumber,@Param("productName") String productName);



}
