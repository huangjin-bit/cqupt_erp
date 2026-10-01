package com.cqupt.mislab.erpn.controller.teacher;

import com.cqupt.mislab.erpn.commons.constant.TeacherImportGamesEnum;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.po.TeacherPo;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupMemberStatus;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.service.forwardquarter.ForwardQuaterService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.gameGroup.MemberDetailService;
import com.cqupt.mislab.erpn.service.order.ChooseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 对应教师管理分组模块
 * @Author xhx
 * @Date 2021/10/7 16:44
 */
@RestController
@RequestMapping("/teacherGroup")
public class TeacherGroupManageController {

    @Autowired
    GroupManageService groupManageService;
    @Autowired
    AdvertisementService advertisementService;
    @Autowired
    ChooseOrderService chooseOrderService;
    @Autowired
    ForwardQuaterService forwardQuaterService;
    @Autowired
    MemberDetailService memberDetailService;

    @RequestMapping("/setGradeRange")
    public Result setGradeRange(@RequestParam("groupName") String groupName, @RequestParam("lowest")Integer low, @RequestParam("highest")Integer high){
        int i = groupManageService.setGradeRange(groupName, low, high);
        if(i == 1){
            return ResultUtil.success("设置成功");
        }else{
            return ResultUtil.error();
        }
    }

    @RequestMapping("/listContribution")
    public Result listMemberDetail(@RequestParam("/userUnique") String userUnique){
        return ResultUtil.success(memberDetailService.listMemberDetailsUnordered(userUnique));
    }

    @RequestMapping("/confirmScore")
    public Result confirmScore(@RequestParam("userUnique") String userUnique){
        List<GameGroupMemberDetail> gameGroupMemberDetails = memberDetailService.listMemberDetailsUnordered(userUnique);
        GameGroupMemberDetail gameGroupMemberDetail = gameGroupMemberDetails.get(0);
        boolean b = memberDetailService.confirmContribution(gameGroupMemberDetail.getGroupName());
        if (b)
            return ResultUtil.success();
        else
            return ResultUtil.error();
    }

    /**
     * 按小组名搜索
     * @param status
     * @param name
     * @param session
     * @return
     */
    @RequestMapping("/search")
    public Result searchGroup(@RequestParam("status")Integer status, @RequestParam("vagueName")String name, HttpSession session) {
        TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
        if (teacherPo == null){
            return ResultUtil.error(TeacherImportGamesEnum.USER_SESSION_EXPIRED.getStatus(),TeacherImportGamesEnum.USER_SESSION_EXPIRED.getMessage());
        }
        if(status == 0) {
            return ResultUtil.success(groupManageService.findUserGroupInfoByVagueName(name,teacherPo.getAdminId()));
        }else {
            return ResultUtil.success(groupManageService.findTeacherGroupInfoByVagueName(name,teacherPo.getAdminId()));
        }
    }

    /**
     * 根据status查找用户信息
     * @param session
     * @return
     */
    @RequestMapping("/showGroups")
    public Result showTeachersGroup(HttpSession session, @RequestParam("status")Integer status){
        TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
        if (teacherPo == null){
            return ResultUtil.error(TeacherImportGamesEnum.USER_SESSION_EXPIRED.getStatus(),TeacherImportGamesEnum.USER_SESSION_EXPIRED.getMessage());
        }
        if(status == 0){
            return ResultUtil.success(groupManageService.showGameGroupByUser(teacherPo.getAdminId()));
        }else {
            return ResultUtil.success(groupManageService.showGameGroupByTeacher(teacherPo.getAdminId()));
        }

    }

    /**
     * 查找历史比赛信息
     * @param session
     * @return
     */
    @RequestMapping("/history")
    public Result showHistoryGameGroup(HttpSession session){
        TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
        if (teacherPo == null){
            return ResultUtil.error(TeacherImportGamesEnum.USER_SESSION_EXPIRED.getStatus(),TeacherImportGamesEnum.USER_SESSION_EXPIRED.getMessage());
        }
        List<GameGroupInfo> gameGroupInfos = groupManageService.showHistoryGameGroups(teacherPo.getAdminId());
        return ResultUtil.success(gameGroupInfos);

    }

    /**
     * 游戏用户基本游戏状态信息
     * @param groupName
     * @return
     */
    @RequestMapping("/query")
    public Result findGameGroupMemberStatusByGroupName(@RequestParam("groupName") String groupName){
        List<GameGroupMemberStatus> gameGroupMemberStatuses = groupManageService.findGameGroupMemberStatusByGroupName(groupName);
        return ResultUtil.success(gameGroupMemberStatuses);
    }

    /**
     * 老师结束经营
     * @param groupName
     * @param userUnique
     * @return
     */
    @RequestMapping("/endPlayGame")
    public Result TeacherEndPlayGame(@RequestParam("groupName")String groupName, @RequestParam("userUnique")String userUnique){
        int result = groupManageService.endPlayGame(groupName, userUnique);
        if(result == 1){
            return ResultUtil.success("结束经营成功");
        }else {
            return ResultUtil.error( "结束失败或此时不能结束经营");
        }
    }

    /**
     * 结束投广告
     * @param userUnique
     * @return
     */
    @RequestMapping("/endAdvertising")
    public Result endAdvertising(@RequestParam("userUnique") String userUnique) {
        Result result = advertisementService.updateAdvertisementFinish(userUnique);
        //TODO xhx ws操作
        return  result;
    }

    /**
     * 结束选订单
     */
    @RequestMapping("/endChooseOrder")
    public Result endChooseOrder(@RequestParam("userUnique") String userUnique){
        int status = chooseOrderService.updateEndChooseOrder(userUnique);
        switch (status) {
            case 1:
                return ResultUtil.success("结束选订单成功");
            case 2:
                return ResultUtil.error(2, "结束该期选订单操作");
            default:
                return ResultUtil.error("请先结束投放广告");
        }
    }

    @RequestMapping("/forwardPeriod")
    public Result ForwardPeriod(@RequestParam("userUnique") String userUnique){
        Result result = forwardQuaterService.forwardStatus(userUnique);
        return result;
    }

}
