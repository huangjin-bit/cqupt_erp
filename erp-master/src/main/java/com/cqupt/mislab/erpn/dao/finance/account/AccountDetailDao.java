package com.cqupt.mislab.erpn.dao.finance.account;

import com.cqupt.mislab.erpn.model.entity.finance.account.AccountDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author whl
 * @date 2021/2/2 - 23:04
 */
@Repository
@Mapper
public interface AccountDetailDao {
    /**
     * 向分录明细表(accountdetail)表中添加一条记录
     * @param accountDetail
     */
    void addAccountDetail(AccountDetail accountDetail);

    /**
     * 获取会计分录详情
     * @param accountId
     * @param userUnique
     * @return
     */
    List<AccountDetail> findAccountDetailsById(@Param("userUnique") String userUnique,@Param("accountId") Integer accountId);
}
