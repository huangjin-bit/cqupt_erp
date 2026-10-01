package com.cqupt.mislab.erpn.model.entity.order;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/2/1 20:50
 */
@Data
public class ChooseOrder {
    private String userUnique;
    private Integer chooseID;
    private Integer period;
    private String marketName;
    private String productName;
    private String groupName;
    private Integer chooseValue;
    public ChooseOrder(){}

    public ChooseOrder(String userUnique, Integer period, String marketName,String productName, String groupName, Integer sequence) {
        this.userUnique = userUnique;
        this.period = period;
        this.marketName = marketName;
        this.productName = productName;
        this.groupName = groupName;
        this.chooseValue = sequence;
    }
}
