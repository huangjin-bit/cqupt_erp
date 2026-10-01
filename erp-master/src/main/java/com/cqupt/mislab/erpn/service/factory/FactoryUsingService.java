package com.cqupt.mislab.erpn.service.factory;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/11 23:01
 */
public interface FactoryUsingService {


    /**
     * 获取已建成的厂房的信息
     * @param userUnique
     * @return
     */
    List<FactoryUsingVO> listFactoryUsingsByUserUnique(String userUnique);


    /**
     * 根据市场获取对应的厂房
     * @param userUnique
     * @param place
     * @return
     */
    Result listFactoryUsingByUserUniqueAndPlace(String userUnique, String place);


    /**
     * 出售已建好的厂房
     * 若当前厂房中存在生产线则不允许出售
     * @return
     */
    Result saleRemoveUsingFactory(String userUnique, Integer factoryId);


}
