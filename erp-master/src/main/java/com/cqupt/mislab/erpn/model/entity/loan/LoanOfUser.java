package com.cqupt.mislab.erpn.model.entity.loan;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/8/12 22:31
 */
@Data
public class LoanOfUser {
    private String userUnique;// 用户在竞赛中的唯一标识，外码，参照 GAMEGROUPMEMBER表中的UserUnique字段， 主码
    private Integer loanId;// 主码，贷款编号
    private String loanTypeName;// 贷款类型
    private Integer beginTime;// 开始贷款的时间
    private Integer endTime;// 归还贷款的时间
    private Integer status;// 0，表示贷款还没有归还，1表示贷款已经归还了
    Integer money;// 贷款的数量
}
