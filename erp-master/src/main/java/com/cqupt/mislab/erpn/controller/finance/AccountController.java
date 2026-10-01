package com.cqupt.mislab.erpn.controller.finance;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.account.AccountDetailService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/8/12 15:33
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountDetailService accountDetailService;


    @ApiOperation("获取会计分录信息")
    @GetMapping("/info")
    public Result getInfo(HttpSession session, @RequestParam(value = "pageIndex",defaultValue ="1" ) Integer pageIndex,
                          @RequestParam(value = "pageSize",defaultValue ="7" ) Integer pageSize, @RequestParam("beginYear") Integer beginYear,
                          @RequestParam("beginPeriod") Integer beginPeriod, @RequestParam("endYear") Integer endYear,
                          @RequestParam("endPeriod") Integer endPeriod) {
        String userUnique = (String) session.getAttribute("userUnique");
        return accountDetailService.findAccountInfoByUserUnique(userUnique, pageIndex, pageSize, beginYear, beginPeriod, endYear, endPeriod);
    }

}
