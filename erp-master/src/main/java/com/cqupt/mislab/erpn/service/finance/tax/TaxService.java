package com.cqupt.mislab.erpn.service.finance.tax;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 * @date 2021/8/12 22:04
 */
public interface TaxService {

    /**
     * 展示用户的税金
     * @param userUnique
     * @return
     */
    Result findTaxSheet(String userUnique);

}
