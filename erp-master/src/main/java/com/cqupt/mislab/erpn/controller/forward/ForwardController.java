package com.cqupt.mislab.erpn.controller.forward;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.forwardquarter.ForwardQuaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/9/8 11:15
 */
@RestController
@RequestMapping("/forward")
public class ForwardController {

    @Autowired
    private ForwardQuaterService forwardQuaterService;

    @GetMapping("/cycle")
    public Result forward(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return forwardQuaterService.forwardStatus(userUnique);
    }
}
