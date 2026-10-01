package com.cqupt.mislab.erpn.service.finance.profit.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.profit.ProfitDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.vo.finance.BalanceVO;
import com.cqupt.mislab.erpn.model.vo.finance.ProfitVO;
import com.cqupt.mislab.erpn.service.finance.profit.ProfitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 20:43
 */
@Service
@Slf4j
public class ProfitServiceImpl implements ProfitService {
    @Autowired
    private ProfitDao profitDao;
    @Autowired
    private CommonDao commonDao;

    @Override
    public Result findProfit(String userUnique, Integer year, Integer period) {

        HashMap<String, Object> ans = new HashMap<>();
        int periodsOfYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        int checkPeriod = period + (year - 1) * periodsOfYear;
        int currentPeriod = commonDao.findCurrentPeriod(userUnique);
        if (checkPeriod > currentPeriod) {
            return ResultUtil.error();
        }
        int curYear = (checkPeriod - 1) / 4 + 1;
        List<ProfitVO> profitSheet = profitDao.findProfitSheet(userUnique, checkPeriod);
        ans.put("curYear", curYear);
        ans.put("profits", profitSheet);
        return ResultUtil.success(ans);
    }
}
