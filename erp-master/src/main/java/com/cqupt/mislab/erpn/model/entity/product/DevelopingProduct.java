package com.cqupt.mislab.erpn.model.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DevelopingProduct {

	/**
	 * 用户唯一标识
	 */
	private String userUnique;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 研发周期
	 */
	private int researchPeriod;
	/**
	 * 每周期需要支付的费用
	 */
	private float researchCost;
	/**
	 * 已经完成的周期
	 */
	private int finishedPeriod;
	/**
	 * 开始时间
	 */
	private int beginTime;
	/**
	 *状态0代表暂停研发，1代表正在研发
	 */
	private int status;

}
