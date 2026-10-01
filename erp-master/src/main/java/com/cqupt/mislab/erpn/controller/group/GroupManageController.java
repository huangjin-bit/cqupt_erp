package com.cqupt.mislab.erpn.controller.group;

import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.GroupCreateDto;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupMemberService;
import com.cqupt.mislab.erpn.service.user.UserService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * @program: com.cqupt.mislab.erpn.controller
 * @description:
 * @author: keji0
 * @date: 2020/8/10 8:45
 */
@Api("分组管理")
@RestController
@RequestMapping("/group")
public class GroupManageController {
    @Autowired
    private GroupManageService groupManageService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private UserService userService;

    @ApiOperation("踢人")
    @RequestMapping(value = "/kick", method = RequestMethod.POST)
    public Result kickMember(@RequestParam("userId") String influencedId, @RequestParam("groupName") String groupName, HttpSession session) {
        String userId = (String) session.getAttribute("userId");
        if (groupMemberService.kickMember(userId, influencedId, groupName, session)) {
            return ResultUtil.success(ResultEnum.SUCCESS);
        }
        return ResultUtil.error(influencedId + "不是" + groupName + "组成员!");
    }

    @ApiOperation("创建分组")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Result createGroup(@RequestBody GroupCreateDto groupCreateDto, HttpSession session) {
        return groupManageService.createGroup(groupCreateDto, session);
    }

    @ApiOperation("加入分组")
    @RequestMapping(value = "/join", method = RequestMethod.POST)
    @ApiImplicitParam(name = "userId", value = "用户标识", required = true, paramType = "query")
    public Result joinGroup(@RequestParam("userId") String userId, @RequestParam("groupName") String groupName, HttpSession session) {
        return groupMemberService.joinGroup(userId, groupName, session);
    }

    @ApiOperation("退出分组")
    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    public Result quitGroup(@RequestParam("userId") String userId, @RequestParam("groupName") String groupName, HttpSession session) {
        return groupMemberService.quitGroup(userId, groupName, session);
    }

    @ApiOperation("获取老师Id列表")
    @RequestMapping(value = "/getTeacherIdList", method = RequestMethod.GET)
    public Result getTeacherIdList() {
        return groupManageService.getTeacherIdList();
    }

    @ApiOperation("获取老师列表")
    @RequestMapping(value = "/getTeacherList", method = RequestMethod.GET)
    public Result getTeacherList() {
        return groupManageService.getTeacherList();
    }

    @ApiOperation("获取分组列表")
    @RequestMapping(value = "/getGroupList", method = RequestMethod.GET)
    public Result getGroupList() {
        return groupManageService.getGroupList();
    }

    @ApiOperation("获取分组成员列表")
    @RequestMapping(value = "/getGroupMemberList", method = RequestMethod.GET)
    @ApiImplicitParam(name = "groupName", value = "分组名称", required = true, paramType = "query")
    public Result getGroupMemberList(@RequestParam("groupName") String groupName, HttpSession session) {
        return groupManageService.getGroupMemberList(groupName, session);
    }

    @ApiOperation("获取组创建者信息")
    @RequestMapping(value = "/getGroupCreator", method = RequestMethod.GET)
    @ApiImplicitParam(name = "groupName", value = "分组名称", required = true, paramType = "query")
    public Result getGroupCreator(@RequestParam("groupName") String groupName) {
        return groupManageService.getGroupCreator(groupName);
    }

    @ApiOperation("开始比赛")
    @RequestMapping(value = "/startGame", method = RequestMethod.POST)
    //TODO 判断身份，只有创建者可以开始比赛，是否写在该接口内
    public synchronized Result startGame(HttpSession session) {
        String groupName = (String) session.getAttribute("groupName");
        Result result = new Result();
        try {
            result = groupManageService.startGame(groupName, session);
        } catch (Exception e) {
            e.printStackTrace();
//            return ResultUtil.error("异常---" + result);
        }


        return result;

    }

    @ApiOperation("解散比赛")
    @GetMapping("/delete")
    @ApiImplicitParam(name = "groupName", value = "分组名称", required = true)
    public Result deleteGroup(@RequestParam("groupName") String groupName, HttpSession session) {
        return groupManageService.deleteGameGroupByGroupName(groupName, session);
    }

    @ApiOperation("结束经营")
    @PostMapping("/stopOperation")
    public Result stopOperation(HttpSession session) {
        return groupMemberService.stopOperation(session);
    }

    @ApiOperation("用于开始比赛修改用户的状态信息")
    @GetMapping("/modifystatus")
    public Result modifystatus(HttpSession session) {
        return groupManageService.modifySessionStatus(session);
    }

    @ApiOperation("用户修改解散分组和踢人后的用户状态")
    @GetMapping("/modifyUserStatus")
    public Result modifyUserStatus(HttpSession session) {
        UserPo userPo = (UserPo) session.getAttribute("userInfo");
        System.out.println(userPo);
        userPo.setStatus(3);
        session.setAttribute("userInfo", userPo);
        return ResultUtil.success();
    }

}
