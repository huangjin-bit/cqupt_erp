package com.cqupt.mislab.erpn.controller.bank;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.loan.LoanOfUser;
import com.cqupt.mislab.erpn.model.vo.loan.LoanOfUserVO;
import com.cqupt.mislab.erpn.service.loan.LoanService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/8/12 22:16
 */
@RestController
@RequestMapping("/loan")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @ApiOperation("获取贷款的基本信息")
    @GetMapping("/info")
    public Result getLoanInfo(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return loanService.findLoanBasicVoByLoanTypeName(userUnique);
    }

    @ApiOperation("申请贷款")
    @PostMapping("/apply")
    public Result applyLoan(HttpSession session, @RequestBody LoanOfUserVO loanOfUserVO) {
        String userUnique = (String) session.getAttribute("userUnique");
       // System.out.println("---->"+userUnique);
        return loanService.applyLoan(userUnique, loanOfUserVO);
    }

    @ApiOperation("获取用户的贷款信息")
    @GetMapping("/loanofuser")
    public Result getLoanOfUser(HttpSession session, @RequestParam(value = "loanTypeName",required = false) String loanTypeName,
                                @RequestParam(value = "status",required = false) Integer status) {
        String userUnique = (String) session.getAttribute("userUnique");
        return loanService.getLoanOfUser(userUnique, loanTypeName, status);
    }

    @ApiOperation("获取一年内到期的长期贷款")
    @GetMapping("/endOneYear")
    public Result endOneYear(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return loanService.findEndOneYear(userUnique);
    }

    @ApiOperation("归还贷款")
    @GetMapping("/returnloan")
    public Result returnLoan(HttpSession session, @RequestParam("loanId") Integer loanId) {
        String userUnique = (String) session.getAttribute("userUnique");
        try {
            return loanService.returnLoan(userUnique, loanId);
        }catch (Exception e){
            return ResultUtil.error();
        }

    }
}
