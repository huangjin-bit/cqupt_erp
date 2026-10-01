package com.cqupt.mislab.erpn.model;

import com.cqupt.mislab.erpn.model.vo.order.ChooseOrderVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/7/25 18:45
 */
@Data
@ToString
@AllArgsConstructor
public class OrderResult {
    //当前可以选择的订单
    List<ChooseOrderVO> chooseOrderVOs;
    //第一次返回订单选择的顺序
    List<String> orders;
    //状态信息
    Integer statusCode;

    public OrderResult(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
