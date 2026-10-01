package com.cqupt.mislab.erpn.model.entity.loan;

import lombok.Data;


/**
 * @author 张烈文
 * @date 2021/8/12 22:31
 */
@Data
public class LoanBasic {
    private String loanTypeName;// 主码，贷款的类型名称
    private Float basicRate;// 贷款利率
    private Integer rateMode;// 支付利息的模式，0表示在每期结束时支付利息，1表示在每年结束时支付利息。
    private Integer permitMode;// 允许进行借贷的时间模式，0表示任意时间可以借贷，1表示年初可以借贷
    private Integer maxValueMode;// 表示借贷的数量是否有限制，0表示没有限制，1表示有限制
    private Integer maxMultiple;// 表示允许贷款的倍数，用于计算可以贷款的最大额度。最大额度=MaxMulitple×所有者权益（上一期）。注意：当MaxValueMode为0时，该属性值没有意义。

}
