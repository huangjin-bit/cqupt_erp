package com.cqupt.mislab.erpn.service.finance.profit;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 * @date 2021/8/12 20:42
 */
public interface ProfitService {


    /**
     * 查询利润表
     * @param userUnique
     * @param year
     * @param period
     * @return
     */
    Result findProfit(String userUnique, Integer year, Integer period);
}
