package com.cqupt.mislab.erpn.service.finance.account.impl;


import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.commons.utils.FinanceUtil;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.finance.account.AccountDetailDao;
import com.cqupt.mislab.erpn.dao.finance.account.AccountHeadDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.finance.account.AccountDetail;
import com.cqupt.mislab.erpn.model.entity.finance.account.AccountHead;
import com.cqupt.mislab.erpn.model.vo.finance.AccountVO;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountDetailServiceImpl implements AccountDetailService {

    @Resource
    private AccountDetailDao accountDetailDao;

    @Resource
    private AccountHeadDao accountHeadDao;
    @Resource
    private CommonDao commonDao;


    @Override
    public void addAccountDetail(String userUnique, String item1, String item2, String itemType1, String itemType2, Double money, Double calValue1, Double calValue2) {
        try {
            if (!StringUtils.isEmpty(userUnique)) {
                AccountDetail accountDetail1 = new AccountDetail(userUnique, accountHeadDao.findAccountId(userUnique), item1, money, itemType1, FinanceUtil.round(calValue1));
                AccountDetail accountDetail2 = new AccountDetail(userUnique, accountHeadDao.findAccountId(userUnique), item2, money, itemType2, FinanceUtil.round(calValue2));
                accountDetailDao.addAccountDetail(accountDetail1);
                accountDetailDao.addAccountDetail(accountDetail2);
                log.info("填写分录明细成功");
            }else {
                log.warn("userUnique为空");
            }
        }catch (Exception e){
            log.error("记录分录明细失败", e);
            throw new IllegalStateException("记录分录明细失败", e);
        }
    }
    @Override
    public void addAccountDetail(String userUnique, String item1, String item2,
                                 String item3, String itemType1, String itemType2, String itemType3,
                                 Double calValue1, Double calValue2, Double calValue3) {
        Double money1 = ERPUtils.round(Math.abs(calValue1));
        Double money2 = ERPUtils.round(Math.abs(calValue2));
        Double money3 = ERPUtils.round(Math.abs(calValue3));
        //lx:四舍五入处理
        AccountDetail accountDetail1 = new AccountDetail(userUnique,
                accountHeadDao.findAccountId(userUnique), item1, money1,
                itemType1, calValue1);
        AccountDetail accountDetail2 = new AccountDetail(userUnique,
                accountHeadDao.findAccountId(userUnique), item2, money2,
                itemType2, calValue2);
        AccountDetail accountDetail3 = new AccountDetail(userUnique,
                accountHeadDao.findAccountId(userUnique), item3, money3,
                itemType3, calValue3);

        accountDetailDao.addAccountDetail(accountDetail1);
        accountDetailDao.addAccountDetail(accountDetail2);
        accountDetailDao.addAccountDetail(accountDetail3);

    }

    @Override
    public Result findAccountInfoByUserUnique(String userUnique, Integer pageIndex, Integer pageSize, Integer beginYear, Integer beginPeriod, Integer endYear, Integer endPeriod) {
        HashMap<String, Object> ans = new HashMap<>();
//        1.查询在[beginYear*周期数+beginPeriod,endYear*周期数+endPeriod]区间内的所有数据
//        1.1）查询每年所含的周期数
        Integer periodsOfOneYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
//        1.2）由于数据表记录的是周期，没有记录年份，因此需要将年份转为周期
        Integer minPeriod = beginPeriod + periodsOfOneYear * (beginYear - 1);
        Integer maxPeriod = endPeriod + periodsOfOneYear * (endYear - 1);
//        1.3）查询在[minPeriod,maxPeriod]内的所有accountId
        PageHelper.startPage(pageIndex, pageSize);
        List<AccountHead> accountHeads = accountHeadDao.findAccountHeadsByUserUnique(userUnique, minPeriod, maxPeriod);
        List<AccountVO> accountVOS = accountHeads.stream().map((accountHead) -> {
            AccountVO accountVO = new AccountVO();
            List<AccountDetail> accountDetailsById = accountDetailDao.findAccountDetailsById(userUnique, accountHead.getAccountId());
            BeanUtils.copyProperties(accountHead, accountVO);
            accountVO.setAccountDetailList(accountDetailsById);
            return accountVO;
        }).sorted((a1, a2) -> {
            return a1.getAccountId() - a2.getAccountId();
        }).collect(Collectors.toList());
//        2.然后分页就可以啦！！！
        Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
        Integer currentYear = (currentPeriod-1) / 4 + 1;
        PageInfo<AccountVO> voPageInfo = new PageInfo<>(accountVOS);
        PageInfo<AccountHead> realPageInfo = new PageInfo<>(accountHeads);
        ans.put("currentYear", currentYear);
        ans.put("pages", realPageInfo.getPages());
        ans.put("curPage", realPageInfo.getPageNum());
        ans.put("count", voPageInfo.getList().size());
        ans.put("accountDetails", voPageInfo.getList());
        return ResultUtil.success(ans);
    }

}
