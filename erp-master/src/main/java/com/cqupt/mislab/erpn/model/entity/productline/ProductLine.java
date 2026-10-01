package com.cqupt.mislab.erpn.model.entity.productline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 张烈文
 * @date 2021/7/11 21:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductLine {
    private String userUnique;// 用户在竞赛中的唯一标识
    private Integer productLineId;// 生产线的ID
    private String factoryId;// 厂房编号，表明该生产线位于哪个厂房内
    private String productLineType;// 生产线类型的名称，如“手工”，“半自动”，“全自动”等
    private Integer status;// 生产线的状态，状态值 0 安装中，1 暂停安装，2 生产中，3 暂停生产，4待生产，5 转产，6 暂停转产
    private String productName;// 正在生产的产品ID
    private Integer productFinishPeriod;// 表示生产线上，在制产品已经完成的生产周期数
    private Integer finishPeriod;// 生产线已完成安装的周期数或者转产完成的周期数
    private Float sellPrice;// 生产线当前的价值。
}
