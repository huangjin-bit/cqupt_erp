package com.cqupt.mislab.erpn.service.market.impl;

import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.market.MarketDao;
import com.cqupt.mislab.erpn.dao.prediction.PredictionOfGroupDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.market.DevelopedMarket;
import com.cqupt.mislab.erpn.model.entity.market.DevelopingMarket;
import com.cqupt.mislab.erpn.model.entity.market.UndevelopMarket;
import com.cqupt.mislab.erpn.model.entity.prediction.PredictionOfGroup;
import com.cqupt.mislab.erpn.service.market.MarketService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resources;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author whl
 * @date 2020/11/22 - 18:17
 */
@Slf4j
@Service
public class MarketServiceImpl implements MarketService {
    @Autowired
    MarketDao marketDao;

    @Autowired
    CommonDao commonDao;

    @Autowired
    private PredictionOfGroupDao predictionOfGroupDao;


    /**
     * 暂停维护一个正在维护的已开拓市场
     * @param userUnique
     * @param marketName
     * @return Result
     */
    @Override
    public Result pauseMaintainDevelopedMarket(String userUnique, String marketName) {
        try {
            if (!StringUtils.isEmpty(userUnique) || !StringUtils.isEmpty(marketName)){
                //获取status:0表示上期未维护,1表示上期进行了维护
                if (marketDao.getDevelopedMarketStatus(userUnique,marketName)==1){
                    //把该市场设为未维护状态(status=0)
                    marketDao.setDevelopedMarketStatusZero(userUnique,marketName);
                    return ResultUtil.error(ResultEnum.SUCCESS);
                }
                //若staus为0,说明该市场本来就没有进行维护,也不可能暂停维护一个已经暂停的市场
                log.error("{}市场本来就是暂停维护状态",marketName);
                return ResultUtil.error(ResultEnum.ERROR);
            }
            log.error("用户唯一标识{}或市场名{}为空",userUnique,marketName);
            return ResultUtil.error(ResultEnum.ERROR);
        }catch (Exception e){
            log.error("暂停维护一个正在维护的已开拓市场失败：{}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR);
        }
    }

    /**
     * 继续维护一个暂停维护的已开拓市场
     * @param userUnique
     * @param marketName
     * @return Result
     */
    @Override
    public Result continueMaintainDevelopedMarket(String userUnique, String marketName) {
        try {
            if (!StringUtils.isEmpty(userUnique) || !StringUtils.isEmpty(marketName)){
                //获取status:0表示上期未维护,1表示上期进行了维护
                if (marketDao.getDevelopedMarketStatus(userUnique,marketName)==0){
                    //设该市场为维护状态(status=1)
                    marketDao.setDevelopedMarketStatusOne(userUnique,marketName);
                    return ResultUtil.error(ResultEnum.SUCCESS);
                }
                //若staus为1,说明该市场本来就在维护,不可能把该市场从暂停变为继续维护
                log.error("{}市场本来就是正在维护状态",marketName);
                return ResultUtil.error(ResultEnum.ERROR);
            }
            log.error("用户唯一标识{}或市场名{}为空",userUnique,marketName);
            return ResultUtil.error(ResultEnum.ERROR);
        }catch (Exception e){
            log.error("继续维护一个暂停维护的已开拓市场失败：{}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR);
        }
    }

    /**
     * 暂停维护一个正在开拓的开拓中市场
     * @param userUnique
     * @param marketName
     * @return Result
     */
    @Override
    public Result pauseDevelopingMarket(String userUnique, String marketName) {
        try {
            if (!StringUtils.isEmpty(userUnique) || !StringUtils.isEmpty(marketName)){
                //获取status:0表示上期没有开拓,1表示上期有进行开拓
                if (marketDao.getDevelopingMarketStatus(userUnique,marketName)==1){
                    //设该市场为暂停开拓状态(status=0)
                    marketDao.setDevelopingMarketStatusZero(userUnique,marketName);
                    return ResultUtil.error(ResultEnum.SUCCESS);
                }
                //若staus为0,说明该市场本来是暂停开拓状态,不可能把该市场从开拓状态变为暂停开拓状态
                log.error("{}市场本来就是暂停开拓状态",marketName);
                return ResultUtil.error(ResultEnum.ERROR);
            }
            log.error("用户唯一标识{}或市场名{}为空",userUnique,marketName);
            return ResultUtil.error(ResultEnum.ERROR);
        }catch (Exception e){
            log.error("暂停维护一个正在开拓的开拓中市场失败：{}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR);
        }
    }

    /**
     * 继续维护一个暂停开拓的开拓中市场
     * @param userUnique
     * @param marketName
     * @return Result
     */
    @Override
    public Result continueDevelopingMarket(String userUnique, String marketName) {
        try {
            if (!StringUtils.isEmpty(userUnique) || !StringUtils.isEmpty(marketName)){
                //获取status:0表示上期没有开拓,1表示上期有进行开拓
                if (marketDao.getDevelopingMarketStatus(userUnique,marketName)==0){
                    //设该市场为正在开拓状态(status=1)
                    marketDao.setDevelopingMarketStatusOne(userUnique,marketName);
                    return ResultUtil.error(ResultEnum.SUCCESS);
                }
                //若staus为1,说明该市场本来是正在开拓状态,不可能把该市场从暂停开拓状态变为正在开拓状态
                log.error("{}市场本来就是正在开拓状态",marketName);
                return ResultUtil.error(ResultEnum.ERROR);
            }
            log.error("用户唯一标识{}或市场名{}为空",userUnique,marketName);
            return ResultUtil.error(ResultEnum.ERROR);
        }catch (Exception e){
            log.error("继续维护一个暂停开拓的开拓中市场失败：{}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR);
        }
    }

    /**
     * 把一个未开拓市场变成开拓中的市场
     * @param userUnique
     * @param marketName
     * @return Result
     */
    @Override
    public Result startUndevelopMarket(String userUnique, String marketName) {
        try {
            if (!StringUtils.isEmpty(userUnique) || !StringUtils.isEmpty(marketName)){
                UndevelopMarket undevelopMarket=marketDao.getUndevelopMarket(userUnique,marketName);
                int currentPeriod = commonDao.findCurrentPeriod(userUnique);
                //注意DevelopingMarket类中有一个构造方法,此构造方法参数中含一个UndevelopMarket对象
                //相当于直接用UndevelopMarket对象的某些属性来初始化DevelopingMarket的某些属性
                DevelopingMarket developMarket = new DevelopingMarket(undevelopMarket,1,currentPeriod,0);
                if (marketDao.addDevelopingMarket(developMarket) && marketDao.deleteUnDevelopingMarket(userUnique,marketName)){
                    return ResultUtil.success(ResultEnum.SUCCESS);
                }
                log.error("增删数据库失败");
                return ResultUtil.error(ResultEnum.ERROR);
            }
            log.error("用户唯一标识{}或市场名{}为空",userUnique,marketName);
            return ResultUtil.error(ResultEnum.ERROR);
        }catch (Exception e){
            log.error("把一个未开拓市场变成开拓中的市场失败：{}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR);
        }
    }

    /**
     * 获取所有市场
     * @param userUnique
     * @return Result
     */
    @Override
    public Result getAllMarket(String userUnique) {
        //开启线程池
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("thread-start-runner-%d").build();
        ExecutorService executor= new ThreadPoolExecutor(3,20,800L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadPoolExecutor.CallerRunsPolicy());
        //开启三个线程进行查询
        Future <List<DevelopedMarket>> future1 =  executor.submit(
            new Callable< List<DevelopedMarket>>() {
                public List<DevelopedMarket> call(){
                return marketDao.getDevelopedMarketListByUserUnique(userUnique);
                }
            }

        );

        Future<List<DevelopingMarket>> future2 =  executor.submit(
            new Callable<  List<DevelopingMarket>>() {
                public  List<DevelopingMarket> call(){
                    return marketDao.getDevelopingMarketListByUserUnique(userUnique);
                }

        });
        Future<List<UndevelopMarket>> future3 =  executor.submit(
                new Callable< List<UndevelopMarket>>() {
                    public  List<UndevelopMarket> call(){
                        return marketDao.getUndevelopMarketListByUserUnique(userUnique);
                    }

                });
        marketDao.getDevelopedMarketListByUserUnique(userUnique);
        List<DevelopedMarket> developedMarketList;
        List<DevelopingMarket> developingMarketList;
        List<UndevelopMarket> undevelopMarketList;
        try {
            //获得三个线程查询结果
            developedMarketList=future1.get();
            developingMarketList= future2.get();
            undevelopMarketList=future3.get();


//            CompletableFuture<List<DevelopedMarket>> future01 = CompletableFuture.supplyAsync(() ->
//                            marketDao.getDevelopedMarketListByUserUnique(userUnique), threadPoolExecutor);
            Map<String,List> marketMap=new HashMap<>();
//            CompletableFuture<List<DevelopedMarket>> future01 = CompletableFuture.supplyAsync(() ->
//                    marketDao.getDevelopedMarketListByUserUnique(userUnique), threadPoolExecutor);
//            CompletableFuture<List<DevelopingMarket>> future02 = CompletableFuture.supplyAsync(() ->
//                    marketDao.getDevelopingMarketListByUserUnique(userUnique), threadPoolExecutor);
//            CompletableFuture<List<UndevelopMarket>> future03 = CompletableFuture.supplyAsync(() ->
//                    marketDao.getUndevelopMarketListByUserUnique(userUnique), threadPoolExecutor);
//            CompletableFuture<Void> future = CompletableFuture.allOf(future01, future02, future03);
//            future.get();
//            List<DevelopedMarket> developedMarketList = future01.get();
//            List<DevelopingMarket> developingMarketList = future02.get();
//            List<UndevelopMarket> undevelopMarketList = future03.get();


/*            List<DevelopedMarket> developedMarketList=marketDao.getDevelopedMarketListByUserUnique(userUnique);
            List<DevelopingMarket> developingMarketList=marketDao.getDevelopingMarketListByUserUnique(userUnique);
            List<UndevelopMarket> undevelopMarketList=marketDao.getUndevelopMarketListByUserUnique(userUnique);*/


            //如果三种市场都没查询到(个数都为0),返回ERROR
            if (developedMarketList.size()==0 && developingMarketList.size()==0 && undevelopMarketList.size()==0){
                log.error("用户{}不在竞赛中",userUnique);
                return ResultUtil.error(ResultEnum.ERROR);
            }
            marketMap.put("developedMarket",developedMarketList);
            marketMap.put("developingMarket",developingMarketList);
            marketMap.put("undevelopMarket",undevelopMarketList);
            return ResultUtil.success(marketMap);
        }catch (Exception e){
            log.error("获取所有市场失败：{}",e.getMessage());
            return ResultUtil.error(ResultEnum.ERROR);
        }

    }

    @Override
    public List<String> findMarketName() {
        return marketDao.findMarketName();
    }

    @Override
    public Result findPredictions(String userUnique, String marketName) {
//        1.通过userUnique游戏组名
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        log.info("userUnique:{}", userUnique);
//        2.通过游戏组名查询出所有预测信息
        List<PredictionOfGroup> predictionOfGroups = predictionOfGroupDao.findAllPredictionOfGroup(groupName);
//        3.判断marketName是否为空，为空则设置为本地市场
        if (StringUtils.isEmpty(marketName)) {
            marketName = "本地市场";
        }
        String finalMarketName = marketName;
//        查询出所有数据后过滤出指定市场的所有数据
        List<PredictionOfGroup> predictionOfMarketName = predictionOfGroups.stream()
                .filter(predictionOfGroup -> Objects.equals(predictionOfGroup.getMarketName(), finalMarketName))
                .collect(Collectors.toList());
        List<List<Integer>> quantity = new ArrayList<>();
        List<List<Double>> price = new ArrayList<>();
        for (int i=0;i<predictionOfMarketName.size();i+=6){
            List<Integer> inQuantity = new ArrayList<>();
            List<Double> inPrice = new ArrayList<>();
            for(int j=0;j<6;j++){
                inQuantity.add(predictionOfMarketName.get(i + j).getMount());
                inPrice.add(predictionOfMarketName.get(i + j).getPrice());
            }
            quantity.add(inQuantity);
            price.add(inPrice);
        }
        HashMap<String, Object> res = new HashMap<>();
        res.put("quantity", quantity);
        res.put("price", price);
        res.put("marketName", marketName);
        log.info("quantity={},price={}", quantity, price);
        return ResultUtil.success(res);

    }
}
