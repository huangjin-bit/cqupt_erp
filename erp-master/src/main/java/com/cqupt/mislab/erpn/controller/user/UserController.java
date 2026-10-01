package com.cqupt.mislab.erpn.controller.user;

import com.cqupt.mislab.erpn.commons.constant.LoginCode;
import com.cqupt.mislab.erpn.commons.constant.LoginMessage;
import com.cqupt.mislab.erpn.commons.constant.LoginResponse;
import com.cqupt.mislab.erpn.commons.utils.IVerifyCodeGen;
import com.cqupt.mislab.erpn.commons.utils.SimpleCharVerifyCodeGenImpl;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.VerifyCode;
import com.cqupt.mislab.erpn.model.dto.LoginForm;
import com.cqupt.mislab.erpn.model.dto.UserStudentInfoRegisterDto;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.user.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * @program: com.cqupt.mislab.erpn.controller
 * @description:
 * @author: keji0
 * @date: 2020/7/20 9:57
 */
@CrossOrigin
@Api("用户")
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupManageService groupManageService;

    //TODO: 查找数据库主键
    @ApiOperation("添加注册")
    @RequestMapping(value = "/regester/addregester",method = RequestMethod.POST)
    public Result addRegisterUser(@RequestBody UserStudentInfoRegisterDto studentInfoRegisterDto){
        return userService.addRegisterUser(studentInfoRegisterDto);
    }

    @ApiOperation("修改用户信息")
    @RequestMapping(value = "/modifyUser",method = RequestMethod.POST)
    public Result modifyUser(@RequestBody UserStudentInfoRegisterDto studentInfoRegisterDto){
        return userService.modifyUser(studentInfoRegisterDto);
    }

    @ApiOperation("生成验证码")
    @RequestMapping(value="/VerifyCode",method = RequestMethod.GET)
    public void verifyCode(HttpServletRequest request, HttpServletResponse response) {
        IVerifyCodeGen iVerifyCodeGen = new SimpleCharVerifyCodeGenImpl();
        Logger logger = Logger.getLogger("code");
        try {
            //设置长宽
            VerifyCode verifyCode = iVerifyCodeGen.generate(80, 28);
            String code = verifyCode.getCode();
            logger.info(code);
            //将VerifyCode绑定session
            request.getSession().setAttribute("VerifyCode", code);
            //设置响应头
//            response.setHeader("Pragma", "no-cache");
            //设置响应头
            response.setHeader("Cache-Control", "no-cache");
            //在代理服务器端防止缓冲
            response.setDateHeader("Expires", 0);
            //设置响应内容类型
            response.setContentType("image/jpeg");
            response.getOutputStream().write(verifyCode.getImgBytes());
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("登录")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public LoginResponse login(@RequestBody LoginForm loginForm, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();

        //移除登录状态，也就是说如果在登录状态进行登录却失败了将被登出登录状态
        session.removeAttribute("userInfo");
        //登录无需验证码校验
        return userService.loginResponse(userService.login(loginForm.getUserId(),loginForm.getPassword(),request,response),loginForm.getUserId(),request,response);
        //登录需要验证码被移除
        /*String sessionVerifyCode = (String) request.getSession().getAttribute("VerifyCode");

        if(!loginForm.getServerCode().equals(sessionVerifyCode)){
            return new LoginResponse<>(LoginCode.WRONG_VERIFYCODE, LoginMessage.WRONG_VERIFYCODE,null);
        }else{
           return userService.loginResponse(userService.login(loginForm.getUserId(),loginForm.getPassword(),request,response),loginForm.getUserId(),request,response);
        }*/
    }


    @ApiOperation("注销")
    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public Result logout(HttpServletRequest request,HttpServletResponse response) {
        return userService.logout(request, response);
    }

    @ApiOperation(value = "通过用户Id获取用户的基本信息",notes = "")
    @GetMapping("/getBasic")
    public LoginResponse getStudentBasicInfo(HttpSession httpSession) {

        String userId = (String) httpSession.getAttribute("userId");
        //试图从Session里面获取缓存数据，由于有缓存这一步，所以在对用户数据进行修改后需要把这个缓存给去除掉
        UserPo userPo = (UserPo) httpSession.getAttribute("userInfo");
        //缓存击中
        if (userPo != null) {
            //判断缓存中的数据是否是需要的数据，看看两个Id是否一致
            if (userId.equals(userPo.getUserId())) {
                //响应前端数据
                return groupManageService.getStudentBasicInfo(httpSession);
            } else {
                //缓存里面的数据不一致，清空错误的缓存
                httpSession.removeAttribute("userId");
                httpSession.removeAttribute("userInfo");
                return new LoginResponse(LoginCode.ILLEGAL,"session中userId与userInfo不一致",null);
            }
        }else {
            return new LoginResponse(LoginCode.ILLEGAL,"无session",null);
        }
    }

    @ApiOperation(value = "开始比赛后获取用户比赛相关相关信息")
    @GetMapping("/info")
    public Result getInfo(HttpSession session, HttpServletRequest request) {
        String userUnique = (String) session.getAttribute("userUnique");
        return userService.getUserInfo(userUnique);
    }

    @ApiOperation("重新开始")
    @PostMapping("/replay")
    public Result replay(HttpSession session){
        String userUnique = (String) session.getAttribute("userUnique");
        return userService.replay(userUnique);
    }
}
