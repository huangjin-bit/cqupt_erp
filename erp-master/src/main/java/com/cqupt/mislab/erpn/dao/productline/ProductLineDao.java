package com.cqupt.mislab.erpn.dao.productline;

import com.cqupt.mislab.erpn.model.entity.productline.ProductLine;
import com.cqupt.mislab.erpn.model.entity.productline.ProductLineBasic;
import com.cqupt.mislab.erpn.model.vo.productline.ProductLineVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 张烈文
 * @date 2021/7/12 14:32
 */
@Mapper
@Repository
public interface ProductLineDao {


    /**
     * 获取指定厂房内所有生产线的信息
     * @param userUnique
     * @param factoryId
     * @return
     */
    List<ProductLine> listProductLineByUserUniqueAndFactoryId(@Param("userUnique") String userUnique,@Param("factoryId") String factoryId);


    /**
     * 为指定厂房新增一条生产线
     * @param productLine
     * @return
     */
    Boolean insertProductLine(ProductLine productLine);

    /**
     * 撤销生产线
     *
     * @param userUnique
     * @param productLineId
     * @return
     */
    Boolean deleteProductLine(@Param("userUnique") String userUnique, @Param("productLineId") Integer productLineId);


    /**
     * 查询基本信息产品信息及指定产品线的
     * @param userUnique
     * @param factoryId
     * @return
     */
    List<ProductLineVO> listProductLineVOByUserUniqueAndFactoryId(@Param("userUnique") String userUnique,@Param("factoryId") String factoryId);


    /**
     * 更新产品线所处状态
     * @param userUnique
     * @param productLineId
     * @param status
     * @return
     */
    Boolean updateStatus(@Param("userUnique") String userUnique,@Param("productLineId") Integer productLineId, @Param("status") Integer status);


    /**
     * 查询指定产品线正在生产的产品名
     *
     * @param userUnique
     * @param productLineId
     * @return
     */
    String findProductNameByProductLineId(@Param("userUnique") String userUnique, @Param("productLineId") Integer productLineId);


    /**
     * 开始转产，更新产品线状态并将finishPeriod清零，修改该产品线待生产的产品
     *
     * @param userUnique
     * @param productLineId
     * @param productName
     * @return
     */
    Boolean updateBeginChangeProductStatus(@Param("userUnique") String userUnique, @Param("productLineId") Integer productLineId, @Param("productName") String productName, @Param("status") Integer satus);


    /**
     * 获取指定的产品线
     *
     * @param userUnique
     * @param productLineId
     * @return
     */
    ProductLine getProductLineByUserUniqueAndProductLineId(@Param("userUnique") String userUnique, @Param("productLineId") Integer productLineId);

    /**
     * 查SellPrice
     * @param userUnique
     * @param productLineId
     * @return
     */
    Float findSellPriceByProductLineId(@Param("userUnique") String userUnique, @Param("productLineId") Integer productLineId);


    List<ProductLineBasic> listProductLineBasicVo();
}
