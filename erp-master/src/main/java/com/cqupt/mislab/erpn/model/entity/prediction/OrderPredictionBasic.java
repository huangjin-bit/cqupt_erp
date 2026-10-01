package com.cqupt.mislab.erpn.model.entity.prediction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/12/22 11:13
 */

@Data
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class OrderPredictionBasic {
    private String productName;
    private double price;
    private int mount;
    private int priceDifference;
    private int mountDifference;
    private double priceFloat;
    private double mountFloat;
}
