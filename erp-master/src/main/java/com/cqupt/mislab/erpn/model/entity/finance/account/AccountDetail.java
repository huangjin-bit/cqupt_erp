package com.cqupt.mislab.erpn.model.entity.finance.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author whl
 * @date 2021/2/2 - 23:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountDetail {
    private String userUnique;//用户在竞赛中的唯一标识，外码，参照 GAMEGROUPMEMBER表中的UserUnique字段
    private Integer accountId;//会计分录的编号,该Id外键关联AccountHead表中的accountId
    private String item;//科目的名称, 外码，参照 ACCOUNTTWOTOONEBASIC表的TwoItem
    private Double money;//金额
    private String itemType;//科目对应的借或贷类型，其值只能是“借”或者“贷”
    private Double calValue;
}
