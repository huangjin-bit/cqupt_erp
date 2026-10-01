package com.cqupt.mislab.erpn.controller.advertisement;

import com.cqupt.mislab.erpn.commons.constant.AdvertiseAndOrderProgress;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementStatusOfUserDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementStatusOfUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 */
@Api("广告")
@RestController
@RequestMapping("/ad")
@Slf4j
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private AdvertisementStatusOfUserService advertisementStatusOfUserService;

    @ApiOperation("获取该用户已经开拓的市场")
    @GetMapping("/getMarketList")
    public Result getDevelopedMarketList(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return advertisementService.findDevelopedMarketByUserUnique(userUnique);
    }

    @ApiOperation("获取产品列表")
    @GetMapping("/getProductByMarket")
    public Result getProductionByMarket(@RequestParam String marketName, HttpSession session) {
        return advertisementService.findAdvertisementByMarketNameAndUserUnique(marketName, session);
    }

    @ApiOperation("投放广告")
    @PostMapping("/putIntoAdvertisement")
    public Result putIntoAdvertisement( Integer adId, Double money) {
        return advertisementService.updateAdvertisementForProduct(adId, money);
    }

    @ApiOperation("获取已经投放的广告列表")
    @GetMapping("/getAlreadyAd")
    public Result getAlreadyAd(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return advertisementService.findAlreadyAdvertisement(userUnique);
    }

    @ApiOperation("广告投放确认")
    @PostMapping("/confirm")
    public Result confirm(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return advertisementService.updateAdvertisementFinish(userUnique);
    }

    @ApiOperation("进度条控制")
    @GetMapping("/getCurrentProgress")
    public Result getCurrentProgress(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return advertisementStatusOfUserService.findAdvertisementStatusOfUserByUserUnique(userUnique);
    }

    @ApiOperation("获取组员投放广告的信息")
    @GetMapping("/getMembersAdInfo")
    public Result backUpdate(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        log.info("userUnique:{}", userUnique);
        return ResultUtil.success(advertisementStatusOfUserService.findMembersCurProgress(userUnique));
    }
}
