package com.cqupt.mislab.erpn.service.productline;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.productline.ProductLineDTO;

/**
 * @author 张烈文
 * @date 2021/7/15 17:59
 */
public interface ProductLineService {


    
    /**
     *
     * 新增一条产品线
     * @param userUnique
     * @param productLineDTO
     * @return
     */
    Result insertProductLine(String userUnique, ProductLineDTO productLineDTO);


    /**
     * 撤销正在安装的生产线
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result removeInstallProductLine(String userUnique, Integer productLineId);


    /**
     *
     * 暂停安装
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result UpdateSuspendedInstallation(String userUnique, Integer productLineId);

    /**
     * 开始安装
     *
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result updateBeginInstallation(String userUnique, Integer productLineId);


    /**
     * 开始生产
     *
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result updateBeginProduct(String userUnique, Integer productLineId);


    /**
     * 恢复生产
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result updateRecoverProduct(String userUnique, Integer productLineId);


    /**
     * 暂停生产
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result updateSuspendedProduct(String userUnique, Integer productLineId);


    /**
     * 开始转产
     *
     * @param userUnique
     * @param productLineId
     * @param productName
     * @return
     */
    Result updateBeginChangeProduct(String userUnique, Integer productLineId, String productName);


    /**
     * 暂停转产
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result updateSuspendChangeProduct(String userUnique, Integer productLineId);


    /**
     * 恢复转产
     * @param userUnique
     * @param productLineId
     * @return
     */
    Result updateRecoverChangeProduct(String userUnique, Integer productLineId);

    /**
     * 出售生产线
     * @param userUnique
     * @param productLineId
     * @param productLineType
     * @return
     */
    Result deleteSaleProductLine(String userUnique, Integer productLineId, String productLineType);

    /**
     * 获取生产线基本信息
     * @return
     */
    Result basicProductLineVo();
}
