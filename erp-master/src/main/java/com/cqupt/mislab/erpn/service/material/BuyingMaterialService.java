package com.cqupt.mislab.erpn.service.material;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.material.BuyingMaterial;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/16 0:52
 */
public interface BuyingMaterialService {


    /**
     *
     * 查询未到货的原料采购单(分页)
     * @param userUnique
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Result listBuyingMaterialUnReach(String userUnique, Integer pageIndex, Integer pageSize);

    /**
     * 新增购买原料的订单
     * @param userUnique
     * @param materialName
     * @param mNumber
     * @return
     */
    Result insertBuyingMaterial(String userUnique, String materialName, Integer mNumber);
}
