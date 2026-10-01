package com.cqupt.mislab.erpn.service.finance.balance.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.balance.BalanceSheetDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.vo.finance.BalanceVO;
import com.cqupt.mislab.erpn.service.finance.balance.BalanceSheetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 20:15
 */
@Service
@Slf4j
public class BalanceSheetServiceImpl implements BalanceSheetService {
    @Autowired
    private BalanceSheetDao balanceSheetDao;
    @Autowired
    private CommonDao commonDao;
    @Override
    public Result findBalanceSheet(String userUnique, Integer year, Integer period) {
        HashMap<String, Object> ans = new HashMap<>();
        int periodsOfYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        int checkPeriod = period + (year - 1) * periodsOfYear;
        int currentPeriod = commonDao.findCurrentPeriod(userUnique);
        if (checkPeriod > currentPeriod) {
            return ResultUtil.error();
        }
        int curYear = (checkPeriod - 1) / 4 + 1;
        List<BalanceVO> balanceSheet = balanceSheetDao.findBalanceSheet(userUnique, checkPeriod);
        ans.put("curYear", curYear);
        ans.put("balanceSheets", balanceSheet);
        return ResultUtil.success(ans);
    }

}
