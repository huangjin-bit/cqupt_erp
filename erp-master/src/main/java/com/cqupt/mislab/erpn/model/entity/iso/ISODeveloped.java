package com.cqupt.mislab.erpn.model.entity.iso;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2020/11/28 11:24
 */
@Data
public class ISODeveloped {
    private String userUnique;
    private String isoName;
    private int status;//状态 0 正在维护，1暂停维护
    private int beginTime;//开发开始时间
    private int endTime;//开发完成时间
    private int lastStatus;//状态 ：上一周期是否支付了维持费，0 支付，1 未支付
    private Double maintainCost;//每周期维护费用
}
