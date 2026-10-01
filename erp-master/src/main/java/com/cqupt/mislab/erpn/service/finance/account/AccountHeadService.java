package com.cqupt.mislab.erpn.service.finance.account;



import com.cqupt.mislab.erpn.model.entity.finance.account.AccountHead;

import java.util.List;
import java.util.Map;

public interface AccountHeadService {

    /**
     * 填写分录头表(ACCOUNTHEAD)
     *
     * @param userUnique  用户在竞赛中的唯一标识
     * @param happenTime  发生此事件的时间，即第几期
     * @param description 产生此分录的说明
     * @author 毛家杰
     */
    public void addAccountHead(String userUnique, int happenTime,
                               String description);


}
