package com.cqupt.mislab.erpn.model.entity.iso;

import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @Author xhx
 * @Date 2020/11/20 20:57
 */

@Data
public class ISOBasic {
    private String isoName;
    private Integer status;
    private Integer researchPeriod;
    private Double researchCost;
    private Double maintainCost;
}
