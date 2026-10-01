package com.cqupt.mislab.erpn.commons.utils.strategyfactory;

import com.cqupt.mislab.erpn.model.vo.factory.FactoryMakingVO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryRentingVO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;

import java.util.List;
import java.util.Map;

/**
 *
 * 策略模式+简单工厂结合
 * @author 张烈文
 * @date 2021/7/14 14:39
 */
public class StrategyContext {

    private FactoryResult result;
    private List<FactoryMakingVO> factoryMakingVOS ;
    private List<FactoryRentingVO> factoryRentingVOS ;
    private List<FactoryUsingVO> factoryUsingVOS ;
    public StrategyContext(Integer status) {
        switch (status) {
            case 1:
                result = new AllFactory();
                break;
            case 2:
                result = new MakingFactory();
                break;
            case 3:
                result = new UsingFactory();
                break;
            case 4:
                result = new RentingFactory();
                break;
        }
    }



    public Map<String, Object> getAnsFactory(String userUnique, String place,Map<String,Object> factoryMap) {

        return result.listFactory(factoryMap, place);
    }

}
