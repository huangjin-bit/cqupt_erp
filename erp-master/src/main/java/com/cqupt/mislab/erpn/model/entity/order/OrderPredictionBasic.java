package com.cqupt.mislab.erpn.model.entity.order;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/2/1 20:52
 */
@Data
public class OrderPredictionBasic {
    private String productName;
    private double price;
    private int mount;
    private int priceDifference;
    private int mountDifference;
    private double priceFloat;
    private double mountFloat;
}
