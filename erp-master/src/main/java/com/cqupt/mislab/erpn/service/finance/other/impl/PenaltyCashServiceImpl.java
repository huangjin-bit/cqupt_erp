package com.cqupt.mislab.erpn.service.finance.other.impl;

import com.cqupt.mislab.erpn.commons.utils.ERPUtils;
import com.cqupt.mislab.erpn.model.entity.order.OrdersOfUser;
import com.cqupt.mislab.erpn.service.finance.other.PenaltyCashService;
import com.cqupt.mislab.erpn.service.order.OrdersOfUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 张烈文
 * @date 2021/8/10 0:00
 */
@Service
public class PenaltyCashServiceImpl implements PenaltyCashService {
    @Autowired
    private OrdersOfUserService ordersOfUserService;
    @Override
    public double computePenaltyCash(String orderId) {
        OrdersOfUser ordersOfUser = ordersOfUserService.findOrderDetail(orderId);
        //lx:四舍五入处理
        double penaltyCash = ERPUtils.round(ordersOfUser.getPenalPercent() * ordersOfUser.getPNumber() * ordersOfUser.getPrice() * (ordersOfUser.getEndTime() - ordersOfUser.getNeedTime()));
        return penaltyCash;
    }
}
