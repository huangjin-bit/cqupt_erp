package com.cqupt.mislab.erpn.commons.config;

import com.cqupt.mislab.erpn.commons.constant.GroupManageConstant;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.service.user.UserService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author whl
 * @date 2020/12/9 - 18:19
 */
@Slf4j
@Component
public class HandshakeInterceptorForWebSocket implements HandshakeInterceptor {
    @Autowired UserService userService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info("拦截器：beforeHandshake");
        try {
            //转换获取HttpServletRequest对象来获取请求参数
            final HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            //获取指定的请求参数
            final String groupName = String.valueOf(httpServletRequest.getParameter(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME));
            //比赛校验是否存在
            //TODO 加入分组不存在的情况，建立连接
            /**
             * 下面这一块if用于处理groupName是错误的组名的情况（没有这个组）
             * 注意：NoGroup是一个正确的组名
             */
            if (!userService.isExistGameGroup(groupName) && groupName!=null && !"NoGroup".equals(groupName)){
                //获取响应对象
                final HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
                final Result responseMessage = ResultUtil.error("比赛分组不存在");
                //设置必要响应头并进行数据响应
                servletResponse.setContentType("application/json;charset=UTF-8");
                servletResponse.getWriter().print(responseMessage);
                servletResponse.flushBuffer();
                return false;
            }

            attributes.put(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME,groupName);

            return true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.info("拦截器：afterHandshake");
    }
}
