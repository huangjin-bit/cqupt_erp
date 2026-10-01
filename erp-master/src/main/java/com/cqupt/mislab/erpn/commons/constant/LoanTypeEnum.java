package com.cqupt.mislab.erpn.commons.constant;

import lombok.Getter;

/**
 * @author 张烈文
 */

@Getter
public enum LoanTypeEnum {
    LONG_LOAN("长期贷款"),
    SHORT_LOAN("短期贷款"),
    HIGH_LOAN("高利贷");
    private String loanTypeName;

    LoanTypeEnum(String loanTypeName) {
        this.loanTypeName = loanTypeName;
    }


}
