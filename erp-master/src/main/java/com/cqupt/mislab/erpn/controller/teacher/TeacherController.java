package com.cqupt.mislab.erpn.controller.teacher;

import com.cqupt.mislab.erpn.commons.constant.LoginCode;
import com.cqupt.mislab.erpn.commons.constant.LoginMessage;
import com.cqupt.mislab.erpn.commons.constant.LoginResponse;
import com.cqupt.mislab.erpn.commons.constant.ResultEnum;
import com.cqupt.mislab.erpn.commons.constant.TeacherImportGamesEnum;
import com.cqupt.mislab.erpn.commons.utils.report.WordUtil;
import com.cqupt.mislab.erpn.commons.exception.RefuseConnectionException;
import com.cqupt.mislab.erpn.commons.utils.CSVUtil;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.LoginForm;
import com.cqupt.mislab.erpn.model.dto.teacher.ImportResultRow;
import com.cqupt.mislab.erpn.model.dto.teacher.GameImportRow;
import com.cqupt.mislab.erpn.model.dto.teacher.SystemResetSummary;
import com.cqupt.mislab.erpn.model.dto.teacher.StudentImportRow;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.po.ApprovingUser;
import com.cqupt.mislab.erpn.model.po.TeacherPo;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.report.ReportService;
import com.cqupt.mislab.erpn.service.teacher.TeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

/**
 * @program: com.cqupt.mislab.erpn.controller
 * @description:
 * @author: keji0
 * @date: 2020/7/27 19:42
 */
//TODO 创建一个枚举通用返回信息和util
@Api("老师")
@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {

    //TODO showGroups 搜索小组名接口
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private GroupManageService groupManageService;
    @Autowired
    private ReportService reportService;

    @ExceptionHandler(RefuseConnectionException.class)
    public Result refuseConnectException(){
        return ResultUtil.error("Access Denied");
    }


    @RequestMapping("/viewReport")
    public Result viewReportByTeacher(@RequestParam("groupName")String groupName) {

        return reportService.viewReportByTeacher(groupName);
    }

    @ApiOperation("用户基本信息查看")
    @GetMapping("/logInfo")
    public Result logInfo(HttpSession session){
        TeacherPo teacherPo = (TeacherPo)session.getAttribute("teacherPo");
        if(teacherPo == null){
            return ResultUtil.error(3,"用户未登陆");
        }
        return ResultUtil.success(teacherPo);
    }

    /**
     * 老师下载某小组汇总实验报告。直接返回 .doc 文件流（与学生端一致），
     * 否则前端会收到 JSON 并误存为 .doc，打开后显示为标签/乱码。
     */
    @ApiOperation("老师下载学生报告")
    @RequestMapping(value = "/downloadReport", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> teacherDownloadReport(
            @RequestParam(value = "groupName", required = false) String groupName,
            HttpSession session) {
        TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
        if (teacherPo == null) {
            Result<Object> err = ResultUtil.error(HttpStatus.UNAUTHORIZED.value(), "未登录或会话已过期，请先登录");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }
        if (groupName == null || groupName.trim().isEmpty()) {
            Result<Object> err = ResultUtil.error(HttpStatus.BAD_REQUEST.value(), "小组名称不能为空");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }
        Result<?> result;
        try {
            result = reportService.TeacherDownLoadReport(groupName.trim());
        } catch (Exception e) {
            log.error("老师下载报告异常 groupName={}", groupName, e);
            Result<Object> err = ResultUtil.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "报告生成失败，请稍后重试");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
        if (result.getStatus() == null || !ResultEnum.SUCCESS.getStatus().equals(result.getStatus())) {
            String msg = result.getMessage() != null ? result.getMessage() : "报告生成失败，请稍后重试";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResultUtil.error(HttpStatus.BAD_REQUEST.value(), msg));
        }
        Object data = result.getData();
        if (data == null || !(data instanceof HashMap)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultUtil.error(HttpStatus.NOT_FOUND.value(), "报告数据不存在"));
        }
        @SuppressWarnings("unchecked")
        HashMap<String, Object> dataMap = (HashMap<String, Object>) data;
        String filePaths = (String) dataMap.get("path");
        if (filePaths == null || filePaths.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultUtil.error(HttpStatus.NOT_FOUND.value(), "报告路径不存在"));
        }
        String[] split = filePaths.split("/");
        String fileName = split[split.length - 1];
        WordUtil wordUtil = new WordUtil();
        String basePath = wordUtil.getWordBasePath();
        String path = basePath + fileName;
        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResultUtil.error(HttpStatus.NOT_FOUND.value(), "报告文件不存在或尚未生成"));
        }
        Resource resource = new FileSystemResource(file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/msword"));
        headers.setContentLength(file.length());
        String downloadFileName = "报告_" + groupName + ".doc";
        try {
            String encodedName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8.name());
            headers.setContentDispositionFormData("attachment", encodedName);
        } catch (UnsupportedEncodingException e) {
            headers.setContentDispositionFormData("attachment", downloadFileName);
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

/*    @ApiOperation("老师下载学生报告")
    @RequestMapping("/downloadReport")
    public void teacherDownloadReport(@RequestParam("groupName")String groupName,HttpServletResponse response) throws Exception {
        Result<HashMap> result = reportService.TeacherDownLoadReport(groupName);
        HashMap hashMap = result.getData();
        try {
                //从网站上下载文件时，需要知道下载的是哪一个文件，因此需要获取文件名称(包括后缀)
                String logName = (String) hashMap.get("path");
                String temp[]=logName.split("\\/");
                String logNameReq=temp[temp.length-1];
                //关键点，需要获取的文件所在文件系统的目录，定位准确才可以顺利下载文件
                String filePath = "C:\\erpnReport\\document\\" + logNameReq;
                File file = new File(filePath);
                //创建一个输入流，将读取到的文件保存到输入流
                InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();
                // 重要，设置response的Header
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(logNameReq.getBytes()));
                response.setHeader("Content-Length", "" + file.length());
                //octet-stream是二进制流传输，当不知文件类型时都可以用此属性
                response.setContentType("application/octet-stream");
                //跨域请求，*代表允许全部类型
                response.setHeader("Access-Control-Allow-Origin", "*");
                //允许请求方式
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
                //用来指定本次预检请求的有效期，单位为秒，在此期间不用发出另一条预检请求
                response.setHeader("Access-Control-Max-Age", "3600");
                //请求包含的字段内容，如有多个可用哪个逗号分隔如下
                response.setHeader("Access-Control-Allow-Headers", "content-type,x-requested-with,Authorization, x-ui-request,lang");
                //访问控制允许凭据，true为允许
                response.setHeader("Access-Control-Allow-Credentials", "true");
                //创建一个输出流，用于输出文件
                OutputStream oStream = new BufferedOutputStream(response.getOutputStream());
                //写入输出文件
                oStream .write(buffer);
                oStream .flush();
                oStream .close();
                System.out.println("下载日志文件" + logNameReq +"成功");
            } catch (Exception e) {
                System.out.println("下载日志文件出错,错误原因:" + e);
            }


//        return reportService.TeacherDownLoadReport(groupName);
    }*/

    @ApiOperation("筛选用户创建比赛")
    @PostMapping("/selectStuStart")
    public Result selectStudentsStart(@RequestParam("groupName")String gropName,
                                     @RequestParam("teacherId")String teacherId,
                                     @RequestParam("userNumber")Integer number,
                                     @RequestParam("userId")String userIds,HttpSession session) throws Exception {
        if(teacherId == null || gropName == null || number == 0 || userIds == null){
            return ResultUtil.error(TeacherImportGamesEnum.ILLEGAL_ARGUMENT.getStatus(),TeacherImportGamesEnum.ILLEGAL_ARGUMENT.getMessage());
        }
        if (teacherId.trim().isEmpty()) {
            return ResultUtil.error(TeacherImportGamesEnum.TEACHER_ERRO.getStatus(),TeacherImportGamesEnum.TEACHER_ERRO.getMessage());
        }
        GameGroupInfo gameGroupInfo = new GameGroupInfo();
        gameGroupInfo.setGroupName(gropName);
        gameGroupInfo.setTeacherId(teacherId);
        gameGroupInfo.setUserNumbers(number);
        gameGroupInfo.setUserIds(userIds);
        try {
            return groupManageService.startGameByTeacher(gameGroupInfo);
        }catch (IllegalArgumentException e){
            String message = e.getMessage();
            return ResultUtil.error(TeacherImportGamesEnum.PLAYER_IN_GAME.getStatus(),TeacherImportGamesEnum.PLAYER_IN_GAME.getMessage(),message);
        }
    }
    @ApiOperation("批量导入学生")
    @PostMapping("/importstu")
    public Result importStudent(@RequestParam("csvfile") MultipartFile file,
                                @RequestParam("adminId") String adminId) {
        try {
            List<StudentImportRow> rows = CSVUtil.parseStudentImportRows(file);
            List<ImportResultRow> results = teacherService.importStudentsDetailed(rows, adminId);
            return ResultUtil.success(results);
        } catch (IllegalArgumentException e) {
            log.warn("CSV 批量导入文件无效：{}", e.getMessage());
            return ResultUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("CSV 批量导入失败", e);
            return ResultUtil.error("批量导入失败，请稍后重试");
        }
    }
    @ApiOperation("批量导入学生进入游戏")
    @PostMapping("/importStuToGame")
    public Result importGame(@RequestParam("csvfile") MultipartFile file,
                             @RequestParam("adminId") String adminId) {
        try {
            List<GameImportRow> rows = CSVUtil.parseGameImportRows(file);
            return teacherService.importGameStudentsDetailed(rows, adminId);
        } catch (IllegalArgumentException e) {
            log.warn("比赛用户 CSV 无效：{}", e.getMessage());
            return ResultUtil.error(e.getMessage());
        } catch (Exception e) {
            log.error("比赛用户 CSV 导入失败", e);
            return ResultUtil.error("批量导入失败，请稍后重试");
        }
        /*try {
         *//*File transforFile = CSVUtil.transforFile(file);
            log.info("成功转储到本地");*//*
            CSVUtil.parseCsvFile(transforFile);
            List<List<String>> lists = CSVUtil.readApprovedCSV(transforFile, columns);
            boolean b = transforFile.renameTo(new File(String.valueOf(transforFile.getAbsoluteFile()) + transforFile.hashCode() + ".csv"));
            log.info("文件转换完成！");
            if(teacherService.importStudents(lists,adminId))
                return ResultUtil.success();
        }catch (IOException e){
            return ResultUtil.error("格式有误！");
        }*/

    }

    @ApiOperation("批量删除学生")
    @RequestMapping(value = "/deletestus")
    public Result deleteBatchUser(@RequestParam("userIds")String[] ids){
        try {
            if(teacherService.deleteBatchUsers(ids))
                return ResultUtil.success("操作成功");
        } catch (Exception e) {}
        return ResultUtil.error("删除失败！");
    }
    @ApiOperation("删除学生")
    @RequestMapping(value = "/deletestu",method = RequestMethod.GET)
    public Result deleteStudents(@RequestParam("userId")String userId){
        try {
            if(teacherService.deleteApprovedUser(userId))
            return ResultUtil.success("操作成功");
        } catch (Exception e) {}
        return ResultUtil.error("删除失败！");
    }

    @ApiOperation("教师账号登出")
    @GetMapping("/logout")
    public Result logout(HttpSession session){
        session.invalidate();
        return ResultUtil.success("登出成功");
    }

    @ApiOperation("搜索")
    @RequestMapping("/search")
    public Result search(@RequestParam("keyword")String key,@RequestParam("status") Integer status){
        if(status == 1||status == 0)
        return ResultUtil.success(teacherService.rangeSearch(key,status));
        return ResultUtil.error("参数有误");

    }
    @ApiOperation("重置密码")
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestParam("userId")String userId,@RequestParam("status") Integer status){
        if(teacherService.resetPassword(status,userId)){
            return ResultUtil.success();
        }
        return ResultUtil.error("发生异常");
    }

    @ApiOperation("获取页数")
    @RequestMapping(value = "/pages")
    public Result getPages(@RequestParam("status") Integer status,@RequestParam("limit") Integer limit){
        Integer res = null;
        if(status == 0) res = teacherService.countRecordsApprovedUser(limit);
        if(status == 1) res = teacherService.countRecordsRegisterUser(limit);
        if(res != null) return  ResultUtil.success(res);
        return ResultUtil.error("输入参数不合法！");
    }

    @ApiOperation("分页查询")
    @RequestMapping(value = "/pageUsers")
    public Result getPageUsers(@RequestParam("page") Integer page, @RequestParam("status")int status, @RequestParam("limit")Integer limit){
//        List<ApprovedUser> approvedUsers = null;
//        List<RegisterUser> registerUsers = null;
//
//        if(status == 0) approvedUsers = teacherService.pageApprovedUser(page,limit,status);
//        if(status == 1) registerUsers = teacherService.pageRegisterUser(page,limit);
//        if(approvedUsers != null){
//            return ResultUtil.success(approvedUsers);
//        }
//        if(registerUsers != null){
//            return ResultUtil.success(registerUsers);
//        }
        List res = teacherService.pageUser(page,limit,status);
        if(res == null) return ResultUtil.error("输入页数不合法！");
        return ResultUtil.success(res);
    }

    @ApiOperation("获取学生数据")
    @RequestMapping(value = "/getusers")
    public Result usersGet( @RequestParam("status")int status){
        List allUser = teacherService.getAllUser(status);
        return  ResultUtil.success(allUser);  
    }

    @ApiOperation("教师登陆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "adminId",value = "管理员账号",required = true,paramType = "login"),
            @ApiImplicitParam(name = "password",value = "管理员密码",required = true,paramType = "login")
    })
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse teacherLogin(@RequestBody LoginForm loginForm, HttpSession session){
        LoginResponse res = LoginResponse.newResponse();
        String code = (String)session.getAttribute("VerifyCode");
//        if(!Objects.equals(code,loginForm.getServerCode())) {
//            return LoginResponse.codeResponse();
//        }
        if(teacherService.teacherLogin(loginForm.getUserId(),loginForm.getPassword(),session)){
            return res.setStatus(LoginCode.TEACHER_SUCCESS).setMessage(LoginMessage.TEACHER_LOGIN_SUCCESS).setData(loginForm.getUserId());
        }
        return res;
    }

    @ApiOperation("取出待审批用户")
    @RequestMapping(value = "/findAllRegister",method = RequestMethod.GET)
    public Result findAllRegister(){
        return teacherService.findAllRegister();
    }

    @ApiOperation("取出审批用户")
    @RequestMapping(value = "/findAllApprovedUser",method = RequestMethod.GET)
    public Result findAllApprovedUser(){
        return teacherService.findAllApprovedUser();
    }

    @ApiOperation("审批用户")
    @RequestMapping(value = "/passRegisterUser",method = RequestMethod.POST)
    public Result passRegisterUser(@RequestBody ApprovingUser user){
        return teacherService.passRegisterUser(user.getUserId(), user.getAdminId());
    }
    @ApiOperation("拒绝用户申请")
    @RequestMapping(value = "/refuseRegisterUser",method = RequestMethod.POST)
    public Result refuseUser(@RequestBody ApprovingUser user){
        return teacherService.refuseRegisterUser(user.getUserId(),user.getAdminId());
    }

    @ApiOperation("批量拒绝用户申请")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds",value = "用户标识",required = true,paramType = "query"),
            @ApiImplicitParam(name = "adminId",value = "管理员标识",required = true,paramType = "query")
    })
    @RequestMapping(value = "/refuseBatchRegisterUsers",method = RequestMethod.POST)
    public Result refuseBatchRegisterUsers(@RequestParam("userIds")String[] userIds,@RequestParam("adminId")String adminId){
        return teacherService.refuseBatchRegisterUsers(userIds, adminId);
    }
    @ApiOperation("批量审批用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds",value = "用户标识",required = true,paramType = "query"),
            @ApiImplicitParam(name = "adminId",value = "管理员标识",required = true,paramType = "query")
    })
    @RequestMapping(value = "/passBatchRegisterUsers",method = RequestMethod.POST)
    public Result passBatchRegisterUsers(@RequestParam("userIds")String[] userIds,@RequestParam("adminId")String adminId){
        return teacherService.passBatchRegisterUsers(userIds, adminId);
    }
    @ApiOperation("清空全部学生并重置系统")
    @PostMapping("/resetSystemData")
    public Result resetSystemData(@RequestBody(required = false) Map<String, String> request, HttpSession session) {
        TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
        if (teacherPo == null) {
            return ResultUtil.error(TeacherImportGamesEnum.USER_SESSION_EXPIRED.getStatus(), TeacherImportGamesEnum.USER_SESSION_EXPIRED.getMessage());
        }
        String confirmText = request == null ? null : request.get("confirmText");
        if (!"确认重置".equals(confirmText)) {
            return ResultUtil.error("请输入确认文字：确认重置");
        }

        try {
            SystemResetSummary summary = teacherService.resetSystemData();
            return ResultUtil.success(summary);
        } catch (Exception e) {
            log.error("系统重置失败", e);
            return ResultUtil.error("系统重置失败，已回滚");
        }
    }
}


