package com.cqupt.mislab.erpn.commons.utils.strategyfactory;

import com.cqupt.mislab.erpn.commons.constant.FactoryStatusEnum;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryMakingVO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryRentingVO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/7/14 14:43
 */
public class AllFactory implements FactoryResult {

    @Override
    public Map<String, Object> listFactory(Map<String, Object> factories, String place) {
        List<FactoryMakingVO> factoryMakingVOS = (List<FactoryMakingVO>) factories.get("makingFactory");
        List<FactoryRentingVO> factoryRentingVOS = (List<FactoryRentingVO>) factories.get("rentingFactory");
        List<FactoryUsingVO> factoryUsingVOS = (List<FactoryUsingVO>) factories.get("usingFactory");
        if (!FactoryStatusEnum.ALL_MARKET.getPlace().equals(place)) {
            List<FactoryMakingVO> filterMaking = factoryMakingVOS.stream()
                    .filter(factoryMakingVO -> Objects.equals(factoryMakingVO.getPlace(), place))
                    .collect(Collectors.toList());
            List<FactoryRentingVO> filterRenting = factoryRentingVOS.stream()
                    .filter(factoryRentingVO -> Objects.equals(factoryRentingVO.getPlace(), place))
                    .collect(Collectors.toList());
            List<FactoryUsingVO> filterUsing = factoryUsingVOS.stream()
                    .filter(factoryUsingVO -> Objects.equals(factoryUsingVO.getPlace(), place))
                    .collect(Collectors.toList());
            factories.put("makingFactory", filterMaking);
            factories.put("rentingFactory", filterRenting);
            factories.put("usingFactory", filterUsing);
        }
        return factories;
    }
}
