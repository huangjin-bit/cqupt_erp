package com.cqupt.mislab.erpn.controller.finance;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.profit.ProfitService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/8/12 20:30
 */
@RestController
@RequestMapping("/profit")
public class ProfitSheetController {

    @Autowired
    private ProfitService profitService;

    @ApiOperation("查询利润表")
    @GetMapping("/info")
    public Result profitInfo(HttpSession session, @RequestParam("year") Integer year,
                             @RequestParam("period") Integer period) {
        String userUnique = (String) session.getAttribute("userUnique");
        return profitService.findProfit(userUnique, year, period);
    }
}
