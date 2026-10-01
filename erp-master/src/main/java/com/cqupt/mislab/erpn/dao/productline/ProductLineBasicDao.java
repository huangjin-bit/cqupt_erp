package com.cqupt.mislab.erpn.dao.productline;

import com.cqupt.mislab.erpn.model.entity.productline.ProductLineBasic;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/12 14:33
 */
@Repository
@Mapper
public interface ProductLineBasicDao {


    /**
     * 获取所有生产线的基本信息
     * @return
     */
    List<ProductLineBasic> listProductLineBasic();


    /**
     *
     * 获取指定类型的生产线基本信息
     * @param productLineType
     * @return
     */
    ProductLineBasic getProductLineBasic(String productLineType);
}
