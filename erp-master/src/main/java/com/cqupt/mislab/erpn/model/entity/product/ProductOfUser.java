package com.cqupt.mislab.erpn.model.entity.product;

import lombok.Data;

/**
 * @author 张烈文
 */
@Data
public class ProductOfUser {
	/**
	 * 用户唯一标识
	 */
	private String userUnique;
	/**
	 * 产品名称
	 */
	private String productName;
	/**
	 * 产品数量
	 */
	private int pNumber;
	/**
     * 仓库名称
	 */
	private String wareHouseName;

}
