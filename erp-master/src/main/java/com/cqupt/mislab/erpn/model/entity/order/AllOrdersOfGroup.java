package com.cqupt.mislab.erpn.model.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author xhx
 * @Date 2021/2/1 20:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllOrdersOfGroup {
    private String orderID;
    private String productName;
    private Integer pNumber;
    private String marketName;
    private Integer needTime;
    private Integer moneyTime;
    private Double penalPercent;
    private String specialRem;
    private String groupName;
    private Integer status;//表示该订单是否已经被选择用户获取了。0表示未被获取，1表示被获取了
    private Integer period;
    //单价
    private Double price;
    public void setAllOrdersOfGroup(String productName, Integer pNumber, Double price,
                                    String marketName, Integer needTime, Integer moneyTime,
                                    Double penalPercent, String specialRem, String groupName,
                                    Integer status, Integer period){
        this.productName = productName;
        this.pNumber = pNumber;
        this.price = price;
        this.marketName = marketName;
        this.needTime = needTime;
        this.moneyTime = moneyTime;
        this.penalPercent = penalPercent;
        this.specialRem = specialRem;
        this.groupName = groupName;
        this.status = status;
        this.period = period;
    }

}
