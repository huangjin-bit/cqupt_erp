package com.cqupt.mislab.erpn.service.factory;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.factory.FactoryRentingDTO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryRentingVO;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/12 20:36
 */
public interface FactoryRentingService {


    /**
     * 获取租借的厂房
     * @param userUnique
     * @return
     */
    List<FactoryRentingVO> listFactoryRentingByUserUnique(String userUnique);


    /**
     *
     * 根据市场名获取所有租借的厂房
     * @param userUnique
     * @param place
     * @return
     */
    Result listFactoryRentingByUserUniqueAndPlace(String userUnique, String place);


    /**
     * 新增租用的厂房
     *
     * @param userUnique
     * @param factoryRentingDTO
     * @return
     */
    Result insertRentingFactory(String userUnique, FactoryRentingDTO factoryRentingDTO);


    /**
     * 移除指定的租用厂房
     * @param userUnique
     * @param factoryId
     * @return
     */
    Result removeRentingFactory(String userUnique, Integer factoryId);
}
