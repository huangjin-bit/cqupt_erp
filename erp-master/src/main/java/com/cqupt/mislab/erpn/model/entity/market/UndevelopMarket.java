package com.cqupt.mislab.erpn.model.entity.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author whl
 * @date 2020/11/19 - 22:46
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UndevelopMarket {
    private String marketName; //市场名称
    private Integer researchPeriod;//开拓该市场需要的周期数
    private Double researchCost;//在开拓过程中，每个周期需要支付的费用
    private Double maintainCost;//市场开拓好后，维护该市场每个周期需要支付的费用
    private String userUnique;//用户唯一标识
}
