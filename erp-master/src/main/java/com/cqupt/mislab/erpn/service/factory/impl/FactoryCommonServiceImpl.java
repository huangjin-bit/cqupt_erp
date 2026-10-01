package com.cqupt.mislab.erpn.service.factory.impl;

import com.cqupt.mislab.erpn.commons.constant.FactoryStatusEnum;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.utils.strategyfactory.StrategyContext;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.market.DevelopedMarket;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryMakingVO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryRentingVO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;
import com.cqupt.mislab.erpn.service.factory.FactoryCommonService;
import com.cqupt.mislab.erpn.service.factory.FactoryMakingService;
import com.cqupt.mislab.erpn.service.factory.FactoryRentingService;
import com.cqupt.mislab.erpn.service.factory.FactoryUsingService;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 张烈文
 * @date 2021/7/13 17:57
 */
@Slf4j
@Service
public class FactoryCommonServiceImpl implements FactoryCommonService {

    @Autowired
    private FactoryMakingService factoryMakingService;
    @Autowired
    private FactoryRentingService factoryRentingService;
    @Autowired
    private FactoryUsingService factoryUsingService;
    @Override
    public Result listFactoryByStatusAndPlace(String userUnique, Integer status, String place) {

        if (StringUtils.isEmpty(userUnique) || status < FactoryStatusEnum.ALL_STATUS.getStatus() || status > FactoryStatusEnum.RENTING_FACTORY.getStatus() || StringUtils.isEmpty(place)) {
            log.error("UserUnique:{} -> status:{} -> place:{}", userUnique, status, place);
            return ResultUtil.error();
        }

        //开启线程池
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-start-runner-%d").build();
        ExecutorService executor= new ThreadPoolExecutor(3,20,800L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.CallerRunsPolicy());


        //开启三个线程查询
        Future <List<FactoryMakingVO>> future1 =  executor.submit(
                new Callable< List<FactoryMakingVO>>() {
                    public List<FactoryMakingVO> call(){
                        return factoryMakingService.listFactoryMakingByUserUnique(userUnique);
                    }
                }

        );

        Future <List<FactoryRentingVO>> future2 =  executor.submit(
                new Callable< List<FactoryRentingVO>>() {
                    public List<FactoryRentingVO> call(){
                        return factoryRentingService.listFactoryRentingByUserUnique(userUnique);
                    }
                }

        );

        Future <List<FactoryUsingVO>> future3 =  executor.submit(
                new Callable< List<FactoryUsingVO>>() {
                    public List<FactoryUsingVO> call(){
                      return   factoryUsingService.listFactoryUsingsByUserUnique(userUnique);
                    }
                }

        );
        HashMap<String, Object> factoryMap = new HashMap<>();

        try {
            List<FactoryMakingVO> factoryMakingVOS = future1.get();
            List<FactoryRentingVO> factoryRentingVOS = future2.get();
            List<FactoryUsingVO> factoryUsingVOS = future3.get();
            factoryMap.put("makingFactory", factoryMakingVOS);
            factoryMap.put("rentingFactory", factoryRentingVOS);
            factoryMap.put("usingFactory", factoryUsingVOS);
//        该处使用策略模式+简单工厂模式

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

//        该处使用策略模式+简单工厂模式
        StrategyContext strategyContext = new StrategyContext(status);
        Map<String, Object> ansFactory = strategyContext.getAnsFactory(userUnique, place, factoryMap);
        return ResultUtil.success(ansFactory);
    }
}
