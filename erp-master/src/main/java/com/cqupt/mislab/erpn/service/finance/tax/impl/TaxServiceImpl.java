package com.cqupt.mislab.erpn.service.finance.tax.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.finance.tax.TaxSheetDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.tax.TaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 张烈文
 * @date 2021/8/12 22:05
 */
@Service
@Slf4j
public class TaxServiceImpl implements TaxService {

    @Autowired
    private TaxSheetDao taxSheetDao;
    @Override
    public Result findTaxSheet(String userUnique) {
        return ResultUtil.success(taxSheetDao.findTaxSheetByUserUnique(userUnique));
    }

}
