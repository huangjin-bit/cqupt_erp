package com.cqupt.mislab.erpn.model.entity.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author whl
 * @date 2020/11/19 - 22:50
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DevelopingMarket {
    private Integer researchPeriod; //开拓该市场需要的周期数
    private Double researchCost; //在开拓过程中，每个周期需要支付的费用
    private Integer finishedPeriod; //已经完成的开拓周期数
    private Integer beginTime;//开始开拓该市场的时间，使用总第几个季度来表示  1,2,3,4,5,6....
    private Integer status;//市场开拓的状态，0表示当前暂停了对该市场的开拓，1表示当前继续对该市场进行开拓
    private String userUnique;//用户唯一标识
    private String marketName;//市场名称

    public  DevelopingMarket(UndevelopMarket market, int status, int beginTime, int finishedPeriod){
        this.userUnique = market.getUserUnique();
        this.marketName = market.getMarketName();
        this.researchPeriod = market.getResearchPeriod();
        this.researchCost = market.getResearchCost();
        this.status = status;
        this.beginTime = beginTime;
        this.finishedPeriod = finishedPeriod;
    }
}
