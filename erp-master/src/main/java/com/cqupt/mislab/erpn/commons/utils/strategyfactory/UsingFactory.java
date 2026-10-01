package com.cqupt.mislab.erpn.commons.utils.strategyfactory;

import com.cqupt.mislab.erpn.commons.constant.FactoryStatusEnum;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/7/14 15:05
 */
public class UsingFactory implements FactoryResult{
    @Override
    public Map<String, Object> listFactory(Map<String, Object> factories, String place) {
        List<FactoryUsingVO> factoryUsingVOS = (List<FactoryUsingVO>) factories.get("usingFactory");
        if (!FactoryStatusEnum.ALL_MARKET.getPlace().equals(place)) {
            List<FactoryUsingVO> filterUsing = factoryUsingVOS.stream()
                    .filter(factoryUsingVO -> Objects.equals(factoryUsingVO.getPlace(), place))
                    .collect(Collectors.toList());
            factories.put("usingFactory", filterUsing);
        }
        return factories;
    }
}
