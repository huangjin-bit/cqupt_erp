package com.cqupt.mislab.erpn.controller.order;

import static com.cqupt.mislab.erpn.commons.constant.OrderConstant.ORDER_INCREMENT;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.order.AllOrdersOfGroup;
import com.cqupt.mislab.erpn.model.vo.order.ChooseOrderVO;
import com.cqupt.mislab.erpn.model.statusChange.OrderSequenceStatusEnum;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.service.order.AllOrdersOfGroupService;
import com.cqupt.mislab.erpn.service.order.ChooseOrderService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @Author xhx
 * @Date 2021/2/1 20:45
 */
@RestController
@Slf4j
@RequestMapping("/chooseorder")
public class ChooseOrderController {
    @Autowired
    ChooseOrderService chooseOrderService;
    @Autowired
    AdAndOrderRefreshService adAndOrderRefreshService;
    @Autowired
    AllOrdersOfGroupService allOrdersOfGroupService;


    @RequestMapping("/getmy")
    public Result getMyOrder(HttpSession session){
       String userUnique = (String) session.getAttribute("userUnique");
        if(userUnique == null){
            return ResultUtil.error("lbwnb!");
        }
        List<ChooseOrderVO> chooseOrderByMarketNameAndProductName = chooseOrderService.findChooseOrderByMarketNameAndProductName(userUnique);
        //List<String> firstOrder = chooseOrderService.getFirstOrder(userUnique);
        // List<ChooseOrderVO> chooseOrderByMarketNameAndProductName = chooseOrderService.findChooseOrderByMarketNameAndProductName(userUnique, marketName, productName);
        return ResultUtil.success(chooseOrderByMarketNameAndProductName);
    }

    //TODO 提示失败的信息
    @RequestMapping("/chooseorder")
    public Result chooseOrder(HttpSession session,@RequestParam("orderId")String orderId){
        String userUnique = (String) session.getAttribute("userUnique");
        int result = -2;
        if(orderId != null){
            result = chooseOrderService.updateChoosingOrder(userUnique,orderId);
            //log.info("{} ============ 订单选择的结果",result);
            if(result == OrderSequenceStatusEnum.USER_CHOOSING.getStatusCode()) {
                return ResultUtil.success();
            }
        }
        return ResultUtil.success(result);
    }


    @ApiOperation("结束某个产品的订货会")
    @RequestMapping("/finishOrder")
    public Result finishOrder(HttpSession session, @RequestParam("orderId")String orderId){
        String userUnique = (String)session.getAttribute("userUnique");
        if(userUnique != null){
                chooseOrderService.updateChooseOrderValue(orderId, ORDER_INCREMENT);
                boolean result = chooseOrderService.updateIsCloseAll(userUnique);
                //通知该组其他成员，当前用户订单已经选择完毕
                adAndOrderRefreshService.changeOrder(userUnique);
                return ResultUtil.success(result);
        }
        return ResultUtil.error();
    }

    @ApiOperation("结束某个市场某个产品的订货会")
    @RequestMapping("/finishOrderByMarketNameAndProductName")
    public Result finishOrderByMarketNameAndProductName(HttpSession session, @RequestParam("marketName")String marketName,@RequestParam("productName")String productName){
        String userUnique = (String)session.getAttribute("userUnique");
        if(userUnique != null){
            chooseOrderService.updateChooseOrderChooseValue(userUnique,marketName,productName);
            boolean result = chooseOrderService.updateIsCloseAll(userUnique);
            //通知该组其他成员，当前用户订单已经选择完毕
            adAndOrderRefreshService.changeOrder(userUnique);
            return ResultUtil.success(result);
        }
        return ResultUtil.error();
    }

    @ApiOperation("结束所有的订货会")
    @RequestMapping("/finishAll")
    public Result endAllMarketAndProduct(HttpSession session){
        String userUnique = (String) session.getAttribute("userUnique");
        if(userUnique != null){
            int result = chooseOrderService.updateEndChooseOrder(userUnique);
            if (result == 3) {
                return ResultUtil.error("广告投放尚未完成，无法结束选单");
            }
            adAndOrderRefreshService.finishOrder(userUnique);
        }else{
            return ResultUtil.error();
        }
        return ResultUtil.success();
    }
    @ApiOperation("展示所有订单信息")
    @RequestMapping("/showAll")
    public Result showAllOrderOfUser(HttpSession session){
        String userUnique = (String)session.getAttribute("userUnique");
        if(userUnique != null){
            List<AllOrdersOfGroup> orderOfGroupByUserUnique = allOrdersOfGroupService.findOrderOfGroupByUserUnique(userUnique);
            return ResultUtil.success(orderOfGroupByUserUnique);
        }
        return ResultUtil.error();
    }

    @RequestMapping("/endSelectOrder")
    public Result endChoosingMarketAndProductName(HttpSession session, @RequestParam("sequence") String sequence) {
        String userUnique = (String)session.getAttribute("userUnique");
        if(userUnique == null) return ResultUtil.error();
        if(sequence != null && !sequence.trim().isEmpty()) {
            try {
                chooseOrderService.updateChooseOrderValue(sequence, ORDER_INCREMENT);
                boolean res = chooseOrderService.updateIsCloseAll(userUnique);
                adAndOrderRefreshService.changeOrder(userUnique);
                return ResultUtil.success(res);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ResultUtil.error();
    }

    @RequestMapping("/getOrderDetail")
    public Result getAllChooseOrderDetail(HttpSession session){
        String userUnique = (String)session.getAttribute("userUnique");
        if(userUnique == null) return ResultUtil.error();
        List<ChooseOrderVO> chooseOrderByMarketNameAndProductName = chooseOrderService.findChooseOrderByMarketNameAndProductName(userUnique);
        return ResultUtil.success(chooseOrderByMarketNameAndProductName);
    }
}
