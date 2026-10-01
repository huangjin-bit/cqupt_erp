package com.cqupt.mislab.erpn.controller.market;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.market.MarketService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * @author whl
 * @date 2020/11/22 - 17:21
 */
@Api("市场")
@RestController
@RequestMapping("market")
@Slf4j
public class MarketController {
    @Autowired
    private MarketService marketService;

    @ApiOperation("获取所有市场")
    @GetMapping("getAllMarket")
    public Result getAllMarket(HttpSession session) {
       String userUnique= (String) session.getAttribute("userUnique");
        return marketService.getAllMarket(userUnique);
    }

    @ApiOperation("暂停维护一个已开拓市场")
    @PostMapping("pauseMaintainDevelopedMarket")
    @ApiImplicitParam(name = "marketName", value = "市场名称", required = true)
    public Result pauseMaintainDevelopedMarket(HttpSession session, String marketName) {
        String userUnique= (String) session.getAttribute("userUnique");
        return marketService.pauseMaintainDevelopedMarket(userUnique, marketName);
    }

    @ApiOperation("继续维护一个已开拓市场")
    @PostMapping("continueMaintainDevelopedMarket")
    @ApiImplicitParam(name = "marketName", value = "市场名称", required = true)
    public Result continueMaintainDevelopedMarket(HttpSession session, String marketName) {
        String userUnique = (String) session.getAttribute("userUnqiue");
        return marketService.continueMaintainDevelopedMarket(userUnique, marketName);
    }

    @ApiOperation("暂停开拓一个开拓中的市场")
    @PostMapping("pauseDevelopingMarket")
    @ApiImplicitParam(name = "marketName", value = "市场名称", required = true)
    public Result pauseDevelopingMarket(HttpSession session, String marketName) {
        String userUnique= (String) session.getAttribute("userUnique");
        return marketService.pauseDevelopingMarket(userUnique, marketName);
    }

    @ApiOperation("继续开拓一个开拓中的市场")
    @PostMapping("continueDevelopingMarket")
    @ApiImplicitParam(name = "marketName", value = "市场名称", required = true)
    public Result continueDevelopingMarket(HttpSession session, String marketName) {
        String userUnique= (String) session.getAttribute("userUnique");
        return marketService.continueDevelopingMarket(userUnique, marketName);
    }

    @ApiOperation("开始开拓一个市场")
    @PostMapping("startUndevelopMarket")
    @ApiImplicitParam(name = "marketName", value = "市场名称", required = true)
    public Result startUndevelopMarket(HttpSession session, String marketName) {
        String userUnique = (String) session.getAttribute("userUnique");
        return marketService.startUndevelopMarket(userUnique, marketName);
    }

    @ApiOperation("获取市场预测信息")
    @GetMapping("/predictions")
    public Result findPredictions(@RequestParam String marketName, HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return marketService.findPredictions(userUnique, marketName);
    }
}
