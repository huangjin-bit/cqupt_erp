package com.cqupt.mislab.erpn.controller.iso;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.iso.DevelopedISOService;
import com.cqupt.mislab.erpn.service.iso.DevelopingISOService;
import com.cqupt.mislab.erpn.service.iso.UndevelopISOService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @Author xhx
 * @Date 2020/11/20 21:28
 */
@Api("iso认证")
@RestController
@RequestMapping("/iso")
public class ISOController {
    @Autowired
    DevelopingISOService developingISOService;
    @Autowired
    DevelopedISOService developedISOService;
    @Autowired
    UndevelopISOService undevelopISOService;

    @ApiOperation("获取用户未研发的ISO")
    @GetMapping("/showUndevelop")
    public Result getUndevelop(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return undevelopISOService.findAllISOUndevelop(userUnique);
    }

    @ApiOperation("获取用户研发中的ISO")
    @GetMapping("/showDeveloping")
    public Result getDeveloping(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developingISOService.findAllISODeveloping(userUnique);
    }

    @ApiOperation("获取用户研发完成的ISO")
    @GetMapping("/showDeveloped")
    public Result getDeveloped(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developedISOService.findAllISODeveloped(userUnique);
    }

    @ApiOperation("暂停研发")
    @ApiImplicitParam(name = "productName", required = true)
    @GetMapping("/stopDeveloping")
    public Result stopDeveloping(HttpSession session,
                                 @RequestParam("isoName") String isoName) {
        String  userUnique = (String) session.getAttribute("userUnique");
        return developingISOService.updateISODevelopingStatusToOne(userUnique, isoName);
    }

    @ApiOperation("继续研发")
    @ApiImplicitParam(name = "productName", required = true)
    @GetMapping("/continueDeveloping")
    public Result continueDeveloping(HttpSession session,
                                     @RequestParam("isoName") String isoName) {
        String userUnique= (String) session.getAttribute("userUnique");
        return developingISOService.updateISODevelopingStatusToZero(userUnique, isoName);
    }

    @ApiOperation("继续维护")
    @ApiImplicitParam(name = "productName", required = true)
    @GetMapping("/continueDeveloped")
    public Result continueDeveloped(HttpSession session,
                                    @RequestParam("isoName") String isoName) {
        String userUnique = (String) session.getAttribute("userUnique");
        return developedISOService.updateISODevelopedStatusToZero(userUnique, isoName);
    }
    @ApiOperation("暂停研发")
    @ApiImplicitParam(name = "productName", required = true)
    @GetMapping("/stopDeveloped")
    public Result stopDeveloped(HttpSession session,
                                    @RequestParam("isoName") String isoName){
        String userUnique = (String) session.getAttribute("userUnique");

        return developedISOService.updateISODevelopedStatusToOne(userUnique,isoName);
    }
    @ApiOperation("开始研发一个新的ISO")
    @ApiImplicitParam(name = "productName", required = true)
    @GetMapping("/start")
    public Result startSearch(HttpSession session,
                              @RequestParam("isoName") String isoName){
        //TODO 是否需要删除undevelopISO表中数据
        String userUnique = (String) session.getAttribute("userUnique");

        return developingISOService.addISOToISODeveloping(userUnique,isoName);
    }
}
