package com.cqupt.mislab.erpn.model.vo.gamegroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 该类主要用来记录组员投放广告的情况
 *
 * @author 张烈文
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameGroupMemberAdAndOrderInfoVo {

    private String userUnique;
    private String userName;
    private String userId;
    private String gender;

    //    用来记录当前组员的进度
    //    1--未投放广告
    //    2-已投放广告
    //    3-投放广告完成
    //    4-开始选择订单
    //    5-已经完成选择订单
    private Integer curProcess;
    private String message;


}
