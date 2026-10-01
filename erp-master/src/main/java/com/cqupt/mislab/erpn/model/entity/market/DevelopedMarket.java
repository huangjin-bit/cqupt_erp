package com.cqupt.mislab.erpn.model.entity.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author whl
 * @date 2020/11/19 - 22:53
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class DevelopedMarket {
    private Integer status;//市场维护的状态:0表示当前暂停了对该市场的维护,1表示当前继续对该市场进行维护
    private Integer lastStatus;//表示上一周期是否支付维护费:0表示未支付，1表示进行了支付
    private Integer beginTime;//开始开拓该市场的时间,用总第几个季度来表示
    private Integer endTime;//开拓完成该市场的时间,用总第几个季度来表示
    private Double maintainCost;//为了维持该市场,每个周期需要支付的费用
    private String userUnique;//用户唯一标识
    private String marketName;//市场名称
}
