package com.cqupt.mislab.erpn.service.finance.account.impl;


import com.cqupt.mislab.erpn.dao.finance.account.AccountHeadDao;
import com.cqupt.mislab.erpn.service.finance.account.AccountHeadService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AccountHeadServiceImpl implements AccountHeadService {

    @Resource
    private AccountHeadDao accountHeadDao;


    @Override
    public void addAccountHead(String userUnique, int happenTime,
                               String description) {
        accountHeadDao.addAccountHead(userUnique, happenTime, description);
    }



}
