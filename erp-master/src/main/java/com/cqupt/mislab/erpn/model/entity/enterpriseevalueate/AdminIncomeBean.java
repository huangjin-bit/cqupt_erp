package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/10/7 14:26
 */
@Data
public class AdminIncomeBean {
    private String userUnique;
    private String groupName;
    private String userId;
    private int currentPeriod;
    private double interestRate;//毛利率
    private double businessProfitRate;//营业利润率
    private double totalAssestsIncomeRate;//总资产收益率
    private double assestsIncomeRate;//资产收益率
    private double ownerBenifit;//所有者权益
    /**
     * 新增一个username属性，使得EnterpriseEvaluateAction.showEndValue()接口可以用userName来展示
     *
     * @author 杨青
     * @return
     * @since 2016/11/27
     */
    private String userName;
}
