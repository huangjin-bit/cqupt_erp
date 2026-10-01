package com.cqupt.mislab.erpn.model.vo.loan;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/8/13 13:30
 */
@Data
public class LoanOfUserVO {
    private String loanTypeName;
    private Double money;
    private Integer loanTime;
}
