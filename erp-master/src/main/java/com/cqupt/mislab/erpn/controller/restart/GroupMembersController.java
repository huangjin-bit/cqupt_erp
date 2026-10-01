package com.cqupt.mislab.erpn.controller.restart;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.vo.gamegroup.GameGroupMemberDetailVo;
import com.cqupt.mislab.erpn.service.gameGroup.MemberDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/**
 * @author 张烈文
 * @date 2021/10/25 21:06
 */
@RestController
@RequestMapping("/members")
public class GroupMembersController {


    @Autowired
    private MemberDetailService memberDetailService;

    @GetMapping("/getMemberInfo")
    public Result memberInfo(HttpSession session) {
        String userUnique = (String) session.getAttribute("userUnique");
        return memberDetailService.listMemberDetails(userUnique);
    }

    @PostMapping("/submit")
    public Result saveMemberDetail(HttpSession session, @RequestBody HashMap<String,List<GameGroupMemberDetailVo>> map) {
        String userUnique = (String) session.getAttribute("userUnique");
        List<GameGroupMemberDetailVo> groupMemberDetailVos = map.get("members");
        return memberDetailService.savaGameGroupMemberDetails(userUnique, groupMemberDetailVos);
    }
}
