package com.cqupt.mislab.erpn.model.dto.enterpriseevalueate;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/11/14 15:07
 */
@Data
@AllArgsConstructor
public class MarketShareDTO {
    String userUnique;
    String marketName;
    Integer firstPeriod;
    Integer lastPeriod;

}
