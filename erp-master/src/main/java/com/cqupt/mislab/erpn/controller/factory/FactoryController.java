package com.cqupt.mislab.erpn.controller.factory;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.factory.FactoryMakingDTO;
import com.cqupt.mislab.erpn.model.dto.factory.FactoryRentingDTO;
import com.cqupt.mislab.erpn.model.vo.factory.FactoryUsingVO;
import com.cqupt.mislab.erpn.service.factory.FactoryCommonService;
import com.cqupt.mislab.erpn.service.factory.FactoryMakingService;
import com.cqupt.mislab.erpn.service.factory.FactoryRentingService;
import com.cqupt.mislab.erpn.service.factory.FactoryUsingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/7/12 16:04
 */
@RestController
@RequestMapping("/factory")
public class FactoryController {
    @Autowired
    private FactoryUsingService factoryUsingService;

    @Autowired
    private FactoryMakingService factoryMakingService;

    @Autowired
    private FactoryRentingService factoryRentingService;
    @Autowired
    private FactoryCommonService factoryCommonService;



    @ApiOperation("已修建的厂房")
    @GetMapping("/factoryusing")
    public Result allFactoryUsing(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        List<FactoryUsingVO> factoryUsingVOS = factoryUsingService.listFactoryUsingsByUserUnique(userUnique);
        return ResultUtil.success(factoryUsingVOS);
    }

    @ApiOperation("正在修建的厂房")
    @GetMapping("/factorymaking")
    public Result allFactoryMaking(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return ResultUtil.success(factoryMakingService.listFactoryMakingByUserUnique(userUnique));
    }

    @ApiOperation("获取租用的厂房")
    @GetMapping("/factoryrenting")
    public Result allFactoryRenting(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return ResultUtil.success(factoryRentingService.listFactoryRentingByUserUnique(userUnique));
    }

    @ApiOperation("根据条件筛选厂房")
    @GetMapping("/allfactory")
    public Result allFactory(HttpSession session, Integer status, String place) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryCommonService.listFactoryByStatusAndPlace(userUnique, status, place);
    }

    @ApiOperation("新建厂房")
    @PostMapping("/savemakingfactory")
    public Result saveMakingFactory(@RequestBody FactoryMakingDTO factoryMakingDTO, HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryMakingService.insertMakingFactory(userUnique, factoryMakingDTO);
    }

    @ApiOperation("暂停修建")
    @GetMapping("/stopmakingfactory")
    public Result stopMakingFactory(HttpSession session, Integer factoryId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryMakingService.stopMakingFactory(userUnique, factoryId);
    }

    @ApiOperation("继续修建")
    @GetMapping("/keepmakingfactory")
    public Result keepMakingFactory(HttpSession session, Integer factoryId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryMakingService.keepMakingFactory(userUnique, factoryId);
    }

    @ApiOperation("新增租用厂房")
    @PostMapping("/saverentingfactory")
    public Result insertRentingFactory(HttpSession session, @RequestBody FactoryRentingDTO factoryRentingDTO) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryRentingService.insertRentingFactory(userUnique, factoryRentingDTO);
    }

    @ApiOperation("不再租用指定厂房")
    @GetMapping("/removerentingfactory")
    public Result removeRentingFactory(HttpSession session, Integer factoryId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryRentingService.removeRentingFactory(userUnique, factoryId);
    }

    @ApiOperation("出售厂房")
    @GetMapping("/salefactory")
    public Result saleFactory(HttpSession session,Integer factoryId ) {
        String userUnique = (String) session.getAttribute("userUnique");
        return factoryUsingService.saleRemoveUsingFactory(userUnique, factoryId);
    }

}
