package com.cqupt.mislab.erpn.controller.restart;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.report.Experience;
import com.cqupt.mislab.erpn.service.report.ExperienceService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 * @date 2021/10/6 21:44
 */
@RestController
@RequestMapping("/experience")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    @PostMapping("/addcontext")
    @ApiOperation("填写实验心得")
    public Result addContext(HttpSession session, @RequestBody Experience experience) {
        String userUnique = (String) session.getAttribute("userUnique");
        experience.setUserUnique(userUnique);

        return experienceService.insertContext(experience);
    }

    @GetMapping("context")
    @ApiOperation("实验心得")
    public Result context(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return experienceService.findContext(userUnique);
    }
}
