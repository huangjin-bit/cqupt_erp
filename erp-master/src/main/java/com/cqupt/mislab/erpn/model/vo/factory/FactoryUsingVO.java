package com.cqupt.mislab.erpn.model.vo.factory;

import com.cqupt.mislab.erpn.model.entity.productline.ProductLine;
import com.cqupt.mislab.erpn.model.vo.productline.ProductLineVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/12 14:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FactoryUsingVO {
    private String factoryId;// 正在使用的厂房编号
    private String factoryType;// 厂房类型的名称，如“大厂房”，“小厂房”
    private Integer sellPrice;// 厂房的剩余价值，即厂房的卖价
    private Integer beginTime;// 开始建造厂房(租赁厂房,在建)的时间，总第几期
    private Integer finishTime;// 厂房建造完工的时间，为总第多少期
    private String place;// 厂房建造(租赁厂房，在建厂房)在什么地点，如本地市场、国际市场
    private Integer productLineCapacity; // 可容纳的生产线数量
    private List<ProductLineVO> productLines; // 指定厂房内所有生产线
}
