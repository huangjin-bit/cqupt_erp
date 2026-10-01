package com.cqupt.mislab.erpn.commons.constant;

import lombok.Getter;

@Getter
public enum MaterialEnum {

    INSUFFICIENT_RAW_MATERIAL_INVENTORY("原料库存不足", 1);

    private String message;
    private Integer code;

    MaterialEnum(String message, Integer code) {

        this.message = message;
        this.code = code;
    }
}
