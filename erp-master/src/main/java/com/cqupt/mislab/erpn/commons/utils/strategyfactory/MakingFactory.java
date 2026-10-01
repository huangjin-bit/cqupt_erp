package com.cqupt.mislab.erpn.commons.utils.strategyfactory;

import com.cqupt.mislab.erpn.commons.constant.FactoryStatusEnum;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryMakingVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/7/14 15:05
 */
public class MakingFactory implements FactoryResult{
    @Override
    public Map<String, Object> listFactory(Map<String, Object> factories, String place) {
        List<FactoryMakingVO> factoryMakingVOS = (List<FactoryMakingVO>) factories.get("makingFactory");
        if (!FactoryStatusEnum.ALL_MARKET.getPlace().equals(place)) {
            List<FactoryMakingVO> filterMaking = factoryMakingVOS.stream()
                    .filter(factoryMakingVO -> Objects.equals(factoryMakingVO.getPlace(), place))
                    .collect(Collectors.toList());
            factories.put("makingFactory", filterMaking);
        }
        return factories;
    }
}
