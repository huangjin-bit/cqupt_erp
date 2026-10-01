package com.cqupt.mislab.erpn.controller.finance;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.will.WillReceiveService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/8/12 20:46
 */
@RestController
@RequestMapping("/willreceive")
public class WillReceiveController {

    @Autowired
    private WillReceiveService willReceiveService;
    @ApiOperation("查询应收账款")
    @GetMapping("/info")
    public Result findWillReceive(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return willReceiveService.findWillReceive(userUnique);
    }

    @ApiOperation("处理贴现")
    @GetMapping("/delete")
    public Result deleteWillReceive(HttpSession session, @RequestParam("willReceiveId") Integer willReceiveId) {
        String userUnique = (String) session.getAttribute("userUnique");
        return willReceiveService.discount(userUnique, willReceiveId);
    }



}
