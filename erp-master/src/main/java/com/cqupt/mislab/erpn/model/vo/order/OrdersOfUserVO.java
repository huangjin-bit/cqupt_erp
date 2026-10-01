package com.cqupt.mislab.erpn.model.vo.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/8/9 22:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersOfUserVO {
    private String orderID;//订单编号
    private String productName;//产品名称，外码
    private Integer pNumber;//产品数量
    private Double price;//单价
    private String marketName;//市场名称
    private Integer needTime;//需要的交货的日期，为总第几期
    private Integer moneyTime;//订单交货后，需要几个账期，货款可以到账
    private Double penalPercent;//违约金比率，查过1期交纳1期的违约金，超过多期，进行累计。
    private Integer status;//0-未交货，1-已交货

}
