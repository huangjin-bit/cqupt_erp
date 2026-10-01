package com.cqupt.mislab.erpn.model.entity.finance.profit;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/8/12 17:31
 */
@Data
public class ProfitSheet {

    private String userUnique;// 用户在竞赛中的唯一标识
    private Integer period;// 时间，即总第几期
    private String oneItem;// 最终的账目
    private Double itemValue;// 账目对应的数值
}
