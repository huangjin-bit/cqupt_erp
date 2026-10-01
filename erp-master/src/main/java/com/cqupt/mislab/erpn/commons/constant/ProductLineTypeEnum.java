package com.cqupt.mislab.erpn.commons.constant;

import lombok.Getter;

/**
 * @author 张烈文
 */

@Getter
public enum ProductLineTypeEnum {

    MANUAL_PRODUCT_LINE("手工生产线"),
    FLEXIBLE_PRODUCT_LINE("柔性生产线"),
    SEMI_AUTOMATIC_PRODUCT_LINE("半自动生产线"),
    FULLY_AUTOMATIC_PRODUCT_LINE("全自动生产线");

    private String productLineType;

    ProductLineTypeEnum(String productLineType) {
        this.productLineType = productLineType;
    }
}
