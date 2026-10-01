package com.cqupt.mislab.erpn.model.vo.finance;

import com.cqupt.mislab.erpn.model.entity.finance.account.AccountDetail;
import lombok.Data;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/12 15:13
 */
@Data
public class AccountVO {
    private Integer accountId;//会计分录的编号，使用序列产生。
    private Integer happenTime;//发生此事件的时间，即第几期
    private String accountIdDescription;//产生此分录的说明
    private List<AccountDetail> accountDetailList;
}
