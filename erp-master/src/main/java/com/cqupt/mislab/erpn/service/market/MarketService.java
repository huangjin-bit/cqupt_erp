package com.cqupt.mislab.erpn.service.market;

import com.cqupt.mislab.erpn.model.Result;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author whl
 * @date 2020/11/22 - 17:24
 */
public interface MarketService {

    Result pauseMaintainDevelopedMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    Result continueMaintainDevelopedMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    Result pauseDevelopingMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    Result continueDevelopingMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    Result startUndevelopMarket(@Param("userUnique") String userUnique, @Param("marketName") String marketName);

    Result getAllMarket(String userUnique);

    List<String> findMarketName();

    /**
     * 获取市场预测信息
     * @param userUnique
     * @param marketName
     * @return
     */
    Result findPredictions(String userUnique, String marketName);
}
