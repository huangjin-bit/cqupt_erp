package com.cqupt.mislab.erpn.controller.order;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.order.AllOrdersOfGroupService;
import com.cqupt.mislab.erpn.service.order.OrdersOfUserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * @Author xhx
 * @Date 2021/2/1 20:46
 */
@RestController
@RequestMapping("/orderofusers")
@Slf4j
public class OrdersOfUsersController {
    @Autowired
    AllOrdersOfGroupService allOrdersOfGroupService;
    @Autowired
    private OrdersOfUserService ordersOfUserService;

    @RequestMapping("/getall")
    public Result getAllOrdersOfGroup(HttpSession session){
        String userUnique = (String) session.getAttribute("userUnique");
        log.info(userUnique);
        return ResultUtil.success(allOrdersOfGroupService.findOrderOfGroupByUserUnique(userUnique));
    }

    @ApiOperation("取出用户所有订单")
    @GetMapping("/checkOrders")
    public Result getOrdersOfUser(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return ResultUtil.success(ordersOfUserService.findOrdersOfUserByUserUnique(userUnique));
    }

    @ApiOperation("订单交货")
    @GetMapping("/deliveryOrder")
    public Result deliveryOrder(HttpSession session, String orderId) {
        HashMap<String, Integer> res = new HashMap<>();
        String userUnique = (String) session.getAttribute("userUnique");
        int status = ordersOfUserService.updateDeliverCheck(userUnique, orderId);
        res.put("status", status);
        return ResultUtil.success(res);

    }

}
