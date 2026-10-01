package com.cqupt.mislab.erpn.model.entity.advertisement;

import lombok.Data;

/**
 * @author 张烈文
 */

@Data
public class AdvertisementStatusOfUser {

    private String userUnique;//用户唯一标识符
    private Integer period;//表示时间，总第几期
    private Integer finishAdvertiseFlag;//表示用户是否完成了当前期的广告费投放，0表示未完成，1表示完成
    private Integer finishOrderFlag;//表示用户是否完成了当前期的所有订单选择，0表示未完成，1表示已经完成
    private Integer chooseOrderFlag;//表示用户是否可以开始当前期的选订单工作，0表示选单尚未开始，1表示选单正在进行中。
}
