package com.cqupt.mislab.erpn.model.vo.factory;

import com.cqupt.mislab.erpn.model.entity.productline.ProductLine;
import com.cqupt.mislab.erpn.model.vo.productline.ProductLineVO;
import jdk.internal.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/12 14:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FactoryRentingVO {


    private String factoryId;// 租用的厂房编号
    private String factoryType;// 厂房类型的名称，如“大厂房”，“小厂房”
    private Integer rentCost;// 租用该厂房时，每期需要交纳的租金.
    private Integer needPeriod;// 还需要等待多少周期该租赁的厂房才可以使用。当其值为0时，表示租赁的厂房可用。
    private String place;// 厂房建造(租赁厂房，在建厂房)在什么地点，如本地市场、国际市场
    private List<ProductLineVO> productLines; //厂房内的所有生产线
    private Integer productLineCapacity; //可容纳的生产线数量
    private float sellPrice;  // 厂房的剩余价值，即厂房的卖价
}
