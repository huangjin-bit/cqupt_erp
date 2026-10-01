package com.cqupt.mislab.erpn.model.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author xhx
 * @Date 2021/2/1 20:53
 */
@Data
public class OrdersOfUser {
    private String userUnique;
    private String orderID;//订单编号
    private String productName;//产品名称，外码
    private Integer pNumber;//产品数量
    private Double price;//单价
    private String marketName;//市场名称
    private Integer needTime;//需要的交货的日期，为总第几期
    private Integer moneyTime;//订单交货后，需要几个账期，货款可以到账
    private Double penalPercent;//违约金比率，查过1期交纳1期的违约金，超过多期，进行累计。
    private String specialRem;//特殊要求，如加急或者ISO认证
    private Integer endTime;//表示  订单交货  的日期，当订单未交货时，其值为NULL

    public OrdersOfUser(String userUnique, String orderID, String productName, Integer pNumber, Double price, String marketName, Integer needTime, Integer moneyTime, Double penalPercent, String specialRem, Integer endTime) {
        this.userUnique = userUnique;
        this.orderID = orderID;
        this.productName = productName;
        this.pNumber = pNumber;
        this.price = price;
        this.marketName = marketName;
        this.needTime = needTime;
        this.moneyTime = moneyTime;
        this.penalPercent = penalPercent;
        this.specialRem = specialRem;
        this.endTime = endTime;
    }

    public OrdersOfUser() {
    }
}
