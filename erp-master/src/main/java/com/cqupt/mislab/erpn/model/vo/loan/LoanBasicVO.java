package com.cqupt.mislab.erpn.model.vo.loan;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/8/13 10:17
 */
@Data
public class LoanBasicVO {
    private String loanTypeName;  //贷款的类型
    private Float basicRate;  //年利率
    private Integer permitMode;  // 0-任意时间都可以借贷，1-只能年初借贷
    private Integer beginYear; //当前周期 1-代表年初，0-代表不是年初
    private Integer currentYear; //当前是第几年,该字段用于前端处理到游戏结束所剩年数小于2不能申请长期贷款
    private Integer maxValueMode;// 表示借贷的数量是否有限制，0表示没有限制，1表示有限制
    private Double maxOfLoan; //允许贷款的最大额度
    private Integer permitYear;  //允许贷款的年限
    private Integer permitPeriod; //允许贷款的周期
    private Integer totalYear; //游戏总年数
}
