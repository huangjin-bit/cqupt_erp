package com.cqupt.mislab.erpn.model.entity.finance.will;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/8/9 20:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WillReceive {
    private String userUnique;
    private Integer willReceiveId;
    private Double money;
    private Integer beginTime;
    private Integer endTime;
    private String willReceiveDescription;
    private String note;


}
