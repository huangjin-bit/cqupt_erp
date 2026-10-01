package com.cqupt.mislab.erpn.service.finance.will.impl;

import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.finance.will.WillReceiveDao;
import com.cqupt.mislab.erpn.dao.finance.will.WillReceiveToCashBasicDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import com.cqupt.mislab.erpn.service.finance.will.WillReceiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * @author 张烈文
 * @date 2021/8/9 21:17
 */
@Service
public class WillReceiveServiceImpl implements WillReceiveService {


    @Autowired
    private WillReceiveDao willReceiveDao;
    @Autowired
    private WillReceiveToCashBasicDao willReceiveToCashBasicDao;
    @Autowired
    private AccountHeadService accountHeadService;
    @Autowired
    private AccountDetailService accountDetailService;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private CashDao cashDao;
    @Override
    public Result findWillReceive(String userUnique) {
        HashMap<String, Object> ans = new HashMap<>();
        ans.put("willReceives", willReceiveDao.listWillReceive(userUnique));
        ans.put("rate", willReceiveToCashBasicDao.getRate());
        return ResultUtil.success(ans);
    }

    @Transactional
    @Override
    public Result discount(String userUnique, Integer willReceiveId) {

        // 根据userUnique willReceiveID 获取money
        try {
            Double money = ERPUtils.round(willReceiveDao.getMoney(userUnique, willReceiveId));
            double rate = willReceiveToCashBasicDao.getRate();
            Double calValue1 = ERPUtils.round(money * rate);//lx:四舍五入处理
            Double calValue2 = ERPUtils.round(money - calValue1);
            if (money > 0) {
                accountHeadService.addAccountHead(userUnique,
                        commonDao.findCurrentPeriod(userUnique), "应收账款");
                accountDetailService.addAccountDetail(userUnique, "现金", "财务费用",
                        "应收账款", "借", "借", "贷", calValue1, calValue2, -money);
                cashDao.addCash(userUnique, calValue1);
            }
            // 删除WILLRECEIVE中的记录
            willReceiveDao.deleteWillReceive(userUnique, willReceiveId);
        } catch (Exception e) {
            return ResultUtil.error();
        }
       return ResultUtil.success();
    }

}
