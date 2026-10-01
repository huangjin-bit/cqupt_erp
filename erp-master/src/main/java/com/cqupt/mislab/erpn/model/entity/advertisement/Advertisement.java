package com.cqupt.mislab.erpn.model.entity.advertisement;


import lombok.Data;

/**
 * @author 张烈文
 */
@Data
public class Advertisement {

    private String adId; //投广告费的编号
    private Integer period; //投广告费的时间，总第几期
    private String marketName;//市场名称
    private String productName;//产品名称
    private String userUnique;
    private Double money; //投入的广告费用


}
