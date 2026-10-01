package com.cqupt.mislab.erpn.dao.material;

import com.cqupt.mislab.erpn.model.entity.material.BuyingMaterial;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/16 0:25
 */
@Repository
@Mapper
public interface BuyingMaterialDao {


    /**
     * 获取指定用户所有未到货所有材料订单
     *
     * @param userUnique
     * @param curPeriod
     * @return
     */
    List<BuyingMaterial> listBuyingMaterial(@Param("userUnique") String userUnique, @Param("curPeriod") Integer curPeriod);


    /**
     * 新增原料订单
     *
     * @param buyingMaterial
     * @return
     */
    Boolean insertBuyingMaterial(BuyingMaterial buyingMaterial);
}
