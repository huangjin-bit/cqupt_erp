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
public class FactoryRenting {
    private String userUnique;// 用户在竞赛中的唯一标识
    private String factoryId;// 租用的厂房编号
    private String factoryType;// 厂房类型的名称，如“大厂房”，“小厂房”
    private Integer rentCost;// 租用该厂房时，每期需要交纳的租金.
    private Integer needPeriod;// 还需要等待多少周期该租赁的厂房才可以使用。当其值为0时，表示租赁的厂房可用。
    private String place;// 厂房建造(租赁厂房，在建厂房)在什么地点，如本地市场、国际市场

    private Integer beginTime; //租赁厂房的开始时间，总第几期


}
