package com.cqupt.mislab.erpn.commons.utils.strategyfactory;

import com.cqupt.mislab.erpn.commons.constant.FactoryStatusEnum;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryRentingVO;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/7/14 15:06
 */
public class RentingFactory implements FactoryResult{
    @Override
    public Map<String, Object> listFactory(Map<String, Object> factories, String place) {
     List< FactoryRentingVO > factoryRentingVOS = (List<FactoryRentingVO>) factories.get("rentingFactory");
        if (!FactoryStatusEnum.ALL_MARKET.getPlace().equals(place)) {
            List<FactoryRentingVO> filterRenting = factoryRentingVOS.stream()
                    .filter(factoryRentingVO -> Objects.equals(factoryRentingVO.getPlace(), place))
                    .collect(Collectors.toList());
            factories.put("rentingFactory", filterRenting);
        }
        return factories;
    }
}
