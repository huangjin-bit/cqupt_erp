package com.cqupt.mislab.erpn.commons.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cqupt.mislab.erpn.commons.constant.LoginResponse;
import com.cqupt.mislab.erpn.commons.exception.RefuseConnectionException;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.teacher.TeacherDao;
import com.cqupt.mislab.erpn.model.po.TeacherPo;
import com.cqupt.mislab.erpn.model.po.UserPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: work-Mis.Lab
 * @description: 请求拦截器
 * @author: 宋丽
 * @create: 2019-11-22 21:00
 **/
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            HttpSession session = request.getSession();
            String uri = request.getRequestURI().toString();
            Pattern pattern = Pattern.compile("teacher");
            Matcher matcher = pattern.matcher(uri);
            if (matcher.find()) {
                //log.info(" 进入老师的拦截器" );
                if (matcher.group().equals("teacher") || matcher.group().equals("teacherGroup") || matcher.group().equals("teacherEvaluate")) {
                    TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
                    if (teacherPo != null) {
                        return true;
                    } else {
                        //response.setStatus(403);
                        return true;
                    }
                }
               // return true;
            }  else {
//                //log.info(" 进入学生的拦截器");
                //获取用户信息，如果没有用户信息直接返回提示信息
                UserPo userPo = (UserPo) session.getAttribute("userInfo");
                TeacherPo teacherPo = (TeacherPo) session.getAttribute("teacherPo");
                if (userPo != null || teacherPo != null) {
                    return true;
                }else{
                    HashMap<String, Object> error = new HashMap<>();
//                   2021.9.25 修改鉴权 张烈文&关一方
                    error.put("code", 10001);
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json;charset=utf-8");
//                    response.setStatus(403);
                    ResultUtil.error(error);
                    String s = JSON.toJSONString(error);
                    PrintWriter writer = response.getWriter();
                    writer.append(s);
                    return false;
                }

            }
        return false;

    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}

