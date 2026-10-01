package com.cqupt.mislab.erpn.model.entity.factory;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FactoryMaking {
    private String userUnique;// 用户在竞赛中的唯一标识
    private String factoryId;// 在建的厂房编号
    private String factoryType;// 厂房类型的名称，如“大厂房”，“小厂房”
    private Integer beginTime;// 开第始建造厂房的时间，总几期
    private String place;// 厂房建造(租赁厂房，在建厂房)在什么地点，如本地市场、国际市场
    private Integer status;// 厂房的状态，0表示当前停止对厂房进行建设，1表示当前对厂房进行建设
    private Integer payMode;// 0表示按期支付建造资金的建造方式，1表示一次性付全款的建造方式
    private Integer finishedPeriod;// 已经完成的建设周期数
}
