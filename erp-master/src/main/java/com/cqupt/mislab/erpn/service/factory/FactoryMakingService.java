package com.cqupt.mislab.erpn.service.factory;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.factory.FactoryMakingDTO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryMakingVO;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/12 21:53
 */
public interface FactoryMakingService {


    /**
     * 获取在建厂房
     *
     * @param userUnique
     * @return
     */
    List<FactoryMakingVO> listFactoryMakingByUserUnique(String userUnique);


    /**
     * 根据市场名获取所有正在修建的厂房
     * @param userUnique
     * @param place
     * @return
     */
    Result listFactoryMakingByUserUniqueAndPlace(String userUnique, String place);


    /**
     * 新建厂房
     *
     * @param factoryMakingDTO
     * @return
     */
    Result insertMakingFactory(String userUnique, FactoryMakingDTO factoryMakingDTO);


    /**
     * 暂停修建厂房
     * @param userUnique
     * @param factoryId
     * @return
     */
    Result stopMakingFactory(String userUnique, Integer factoryId);


    /**
     * 继续修建厂房
     * @param userUnique
     * @param factoryId
     * @return
     */
    Result keepMakingFactory(String userUnique, Integer factoryId);

}
