package com.cqupt.mislab.erpn.service.finance.will;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.finance.will.WillReceive;

/**
 * @author 张烈文
 * @date 2021/8/9 21:16
 */
public interface WillReceiveService {

    /**
     * 查询应收账款
     * @param userUnique
     * @return
     */
    Result findWillReceive(String userUnique);

    /**
     * 处理贴现
     * @param userUnique
     * @param willReceiveId
     * @return
     */
    Result discount(String userUnique, Integer willReceiveId);
}
