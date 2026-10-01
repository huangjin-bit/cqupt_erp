package com.cqupt.mislab.erpn.controller.tax;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.finance.tax.TaxService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/8/12 22:02
 */
@RestController
@RequestMapping("/tax")
public class TaxSheetController {

    @Autowired
    private TaxService taxService;

    @ApiOperation("展示用户的税金")
    @GetMapping("/info")
    public Result getTaxInfo(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return taxService.findTaxSheet(userUnique);
    }
}
