package com.cqupt.mislab.erpn.controller.finance;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.balance.BalanceSheetService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/8/12 17:51
 */
@RestController
@RequestMapping("balance")
public class BalanceSheetController {
    @Autowired
    private BalanceSheetService balanceSheetService;

    @ApiOperation("查询资产负债表")
    @GetMapping("/info")
    public Result getBalanceInfo(HttpSession session, @RequestParam("year") Integer year,
                                 @RequestParam("period") Integer period) {
        String userUnique = (String) session.getAttribute("userUnique");
       return balanceSheetService.findBalanceSheet(userUnique, year, period);
    }
 }
