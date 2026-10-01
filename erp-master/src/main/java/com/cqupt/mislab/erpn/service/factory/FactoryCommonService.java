package com.cqupt.mislab.erpn.service.factory;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 * @date 2021/7/13 17:53
 */
public interface FactoryCommonService {


    /**
     * 根据指定条件筛选出厂房
     * @param userUnique
     * @param status 0:获取全部状态的厂房  1:正在修建的厂房 2:租用中的厂房 3:已建成的厂房
     * @param place 0:获取全部市场厂房 1:本地市场 2:区域市场 3:国内市场 4:亚洲市场 5:国际市场
     * @return
     */
    Result listFactoryByStatusAndPlace(String userUnique, Integer status, String place);
}
