package com.cqupt.mislab.erpn.model.entity.product;

import lombok.Data;

/**
 * @author 张烈文
 */
@Data
public class ProductDetailBasic {


    /**
     * 产品名称
	 */
	private String productName;
	/**原材料名称
	 */
	private String materialName;
	/**
     * 原材料数量
	 */
	private int mNumber;
	
}
