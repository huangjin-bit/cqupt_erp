package com.cqupt.mislab.erpn.model.entity.productline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 * @date 2021/7/11 21:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductLineBasic {

    private String productLineType;// 生产线类型的名称，如“手工”，“半自动”，“全自动”等
    private Float setupPeriodPrice;// 生产线每个安装周期的金额
    private Integer setupPeriod;// 生产线的安装周期
    private Integer producePeriod;// 生产线生产产品的效率，即生产1个产品需要的周期
    private Integer changePeriod;// 生产线的转产需要的周期
    private Float changeCost;// 生产线的转产需要的费用
    private Float mainCost;// 生产线每期的维修费用
    private Float stumpCost;// 生产线的残值。即折旧到一定阶段后，无论怎么再折旧，都不再减少的价值
    private Float depreciation;// 生产线投入使用后，每期折旧的价值。完工当期不折旧。
    private Integer delayTime;// 卖掉生产线后，卖生产线的钱需要延长几个账期到账
}
