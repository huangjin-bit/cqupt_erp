package com.cqupt.mislab.erpn.commons.constant;

import lombok.Getter;

/**
 * @author 张烈文
 */
@Getter
public enum FactoryStatusEnum {
    ALL_MARKET(0, "全部"),
    ALL_STATUS(1, "全部状态"),
    MAKING_FACTORY(2, "修建中"),
    USING_FACTORY(3, "使用中"),
    RENTING_FACTORY(4, "租用中");

    private Integer status;
    private String place;

    FactoryStatusEnum(Integer status, String place) {
        this.place = place;
        this.status = status;
    }
}
