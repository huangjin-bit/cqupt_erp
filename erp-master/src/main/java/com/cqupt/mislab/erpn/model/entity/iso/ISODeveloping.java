package com.cqupt.mislab.erpn.model.entity.iso;

import lombok.Data;

/**
 * @Author xhx
 * @Date 2020/11/28 11:26
 */
@Data
public class ISODeveloping {
    private String userUnique;
    private String isoName;
    private int researchPeriod;//开发需要的周期数
    private Double researchCost;//每周期需要的费用
    private int finishedPeriod;//已经开发的周期数
    private int beginTime;//开发开始时间
    private int status;//状态 0 正在开发，1 暂停开发

    public ISODeveloping(String userUnique, String isoName, int researchPeriod, Double researchCost, int finishedPeriod, int beginTime, int status) {
        this.userUnique = userUnique;
        this.isoName = isoName;
        this.researchPeriod = researchPeriod;
        this.researchCost = researchCost;
        this.finishedPeriod = finishedPeriod;
        this.beginTime = beginTime;
        this.status = status;
    }
}
