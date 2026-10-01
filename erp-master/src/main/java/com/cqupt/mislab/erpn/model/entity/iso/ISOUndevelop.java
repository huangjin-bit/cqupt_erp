package com.cqupt.mislab.erpn.model.entity.iso;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2020/11/28 11:26
 */
@Data
public class ISOUndevelop {
    private String userUnique;
    private String isoName;
    private int researchPeriod;//开发需要的周期数
    private Double researchCost;//每周期需要的费用
    private Double maintainCost;//每周期维护需要的费用
}
