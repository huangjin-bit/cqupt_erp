package com.cqupt.mislab.erpn.model.entity.product;

import lombok.Data;

/**
 * @author 张烈文
 */
@Data
public class ProductBasic {
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 产品开发的初始化状态，1表示初始时产品已经开发完成，直接就能生产，0表示初始时没有进行产品开发
	 */
	private Integer status;
	/**
	 * 完成该产品开发需要的周期数
	 */
	private Double researchPeriod;
	/**
     * 在研发产品过程中，每个周期需要支付的开发费用
	 */
	private Double researchCost;
}
