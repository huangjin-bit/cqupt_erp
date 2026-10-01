package com.cqupt.mislab.erpn.model.dto.enterpriseevalueate;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/11/14 15:42
 */
@Data
@AllArgsConstructor
public class ProductShareDTO {
    String userUnique;
    String productName;
    Integer firstPeriod;
    Integer lastPeriod;
}
