package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/11/3 22:12
 */
@Data
public class AdminIncomeBeanNewly {
    private String userUnique;
    private String groupName;
    private String userId;
    private int    currentPeriod;
    private double interestRate;//毛利率
    private double businessProfitRate;//营业利润率
    private double totalAssestsIncomeRate;//总资产收益率
    private double assestsIncomeRate;//资产收益率
    private double ownerBenifit;//所有者权益
    private String userName;
}
