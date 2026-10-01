package com.cqupt.mislab.erpn.commons.utils.strategyfactory;

import java.util.Map;

/**
 * @author 张烈文
 * @date 2021/7/14 14:40
 */
public interface FactoryResult {


    /**
     * 获取指定市场和状态的所有工厂
     * @param factories
     * @param place
     * @return
     */
    Map<String, Object> listFactory(Map<String, Object> factories, String place);
}
