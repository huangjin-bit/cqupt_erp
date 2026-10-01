package com.cqupt.mislab.erpn.service.finance.balance;

import com.cqupt.mislab.erpn.model.Result;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 20:13
 */
public interface BalanceSheetService {

    /**
     * 查询资产负债表
     * @param userUnique
     * @param year
     * @param period
     * @return
     */
    Result findBalanceSheet(String userUnique, Integer year, Integer period);


}
