package com.cqupt.mislab.erpn.model.entity.finance.account;

import lombok.*;

/**
 * @author whl
 * @date 2021/2/2 - 22:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountHead {

    private String userUnique;//用户在竞赛中的唯一标识，外码，参照 GAMEGROUPMEMBER表中的UserUnique字段
    private Integer accountId;//会计分录的编号，使用序列产生。
    private Integer happenTime;//发生此事件的时间，即第几期
    private String accountIdDescription;//产生此分录的说明
}
