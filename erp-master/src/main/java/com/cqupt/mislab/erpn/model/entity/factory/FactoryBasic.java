package com.cqupt.mislab.erpn.model.entity.factory;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FactoryBasic {

    private String factoryType;// 厂房类型的名称，如“大厂房”，“小厂房”
    private Integer makePeriod;// 厂房建造周期，即新建一个厂房需要的周期数
    private Integer makeCost;// 在建造过程中，每个周期需要支付的建造费用
    private Integer cashPrice;// 使用现金一次性支付时厂房的造价
    private String makeDescription;// 对一次性支付建造资金和支付一期资金建造一期资金两种方式进行说明。
    private Float depreciation;// 厂房建造后，每期折旧的价值。完工当期不折旧。
    private Float stumpCost;// 残值，即折旧到一定阶段后，无论怎么再折旧，都不可在减少的价值。
    private Integer capacity;// 厂房可以容纳的生产线数量
    private Integer rentCost;// 租用该厂房时，每期需要交纳的租金
    private Integer rentAvailablePeriod;// 从开始租用厂房起到租用厂房可以实际使用所需要等待的期数。
    private Integer sellDifferentPrice;// 卖厂房时采取一次性收取现金时，卖价的与厂房剩余价值之间差价。
    private Integer delayTime;// 出售发生后延迟给全部现金的账期.
    private String sellDescription;// 对一次性现金出售厂房，或出售厂房等一段账期到账两种出售方式进行说明。
    private Float maintainCost;// 厂房建好后每期的维护费用，完工当期不支付维护费用
}
