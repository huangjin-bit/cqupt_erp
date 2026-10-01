package com.cqupt.mislab.erpn.model.entity.finance.balance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/8/9 20:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceSheet {
    private String userUnique;// 用户在竞赛中的唯一标识，外码，参照GAMEGROUPMEMBER
    private int period;// 时间，即总第几期
    private String oneItem;// 最终的账目
    private double beginValue;// 期初数
    private double endValue;// 期末数

}
