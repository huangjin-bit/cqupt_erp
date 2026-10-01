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
public class FactoryUsing {
    private String userUnique;// 用户在竞赛中的唯一标识
    private String factoryId;// 正在使用的厂房编号
    private String factoryType;// 厂房类型的名称，如“大厂房”，“小厂房”
    private Integer sellPrice;// 厂房的剩余价值，即厂房的卖价
    private Integer beginTime;// 开始建造厂房(租赁厂房,在建)的时间，总第几期
    private Integer finishTime;// 厂房建造完工的时间，为总第多少期
    private String place;// 厂房建造(租赁厂房，在建厂房)在什么地点，如本地市场、国际市场
}
