package com.cqupt.mislab.erpn.model.vo.loan;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/8/13 15:24
 */
@Data
public class EndOneYearLoanOfUser {


    private Integer loanId;
    private String loanTypeName;
    private Integer money;
    private Integer beginYear;
    private Integer endYear;

}
