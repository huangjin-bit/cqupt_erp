//package com.cqupt.mislab.erpn.commons.websocket;
//
//import com.cqupt.mislab.erpn.commons.constant.GroupManageConstant;
//import com.cqupt.mislab.erpn.model.Result;
//import com.cqupt.mislab.erpn.service.user.UserService;
//import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.http.server.ServletServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.AbstractWebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.websocket.server.ServerEndpoint;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * @program: com.cqupt.mislab.erpn.commons.websocket
// * @description:
// * @author: keji0
// * @date: 2020/8/5 2:41
// */
//
//@Slf4j
//@Component
//public class CommonWebsocketService extends AbstractWebSocketHandler implements HandshakeInterceptor,CommonWebsocketServicePublisher {
//
//    @Autowired
//    UserService userService;
//
//
//
//    /**
//     * 用于存储每一个比赛里面的session数据，每一个比赛的所有的session数据被存在同一个集合里面
//     */
//    protected final Map<String, List<WebSocketSession>> webSocketSessionsMap = new ConcurrentHashMap<>();
//
//    /**
//     * @param session
//     * @param message
//     * @description 回显测试的作用
//     * @throws Exception
//     */
//    @Override
//    public void handleMessage(WebSocketSession session,WebSocketMessage<?> message) throws Exception{
//        //这里仅仅是对收到的数据进行显示和回显，主要是用于对websocket的测试，实际应用中这个websocket主要是用于后端主动给前端推送数据，不接受前端数据
//        System.out.println(message.getPayload());
//        session.sendMessage(message);
//    }
//
//    /**
//     * @param session
//     * @description 连接建立后通过比赛信息将该会话保存在对应的数据结构里面
//     * @throws Exception
//     */
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
//        //日志记录，便于调试
//        log.info(session.getAttributes().get(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME) + "连接建立成功！！(afterConnectionEstablished)");
//        //获取连接时拦截下来的连接参数
//        final String groupName = (String) session.getAttributes().get(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME);
//        if(webSocketSessionsMap.get(groupName) == null){
//            //多个比赛的集合初始化是同步的
//            synchronized(this){
//                if(webSocketSessionsMap.get(groupName) == null){
//                    List<WebSocketSession> webSocketSessionList = new ArrayList<>();
//                    webSocketSessionList.add(session);
//                    webSocketSessionsMap.put(groupName,webSocketSessionList);
//                }
//            }
//        }else {
//            //对集合的操作也是同步的
//            synchronized(webSocketSessionsMap.get(groupName)){
//                webSocketSessionsMap.get(groupName).add(session);
//            }
//        }
//    }
//
//    /**
//     * @param session
//     * @param exception
//     * @description 传输异常处理
//     * @throws Exception
//     */
//    @Override
//    public void handleTransportError(WebSocketSession session,Throwable exception) throws Exception{
//        //打印日志
//        exception.printStackTrace();
//        this.afterConnectionClosed(session, CloseStatus.SERVER_ERROR);
//    }
//
//    @Override
//    public void afterConnectionClosed(WebSocketSession session,CloseStatus status) throws Exception{
//        //获取链接
//        final String groupName = (String) session.getAttributes().get(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME);
//        webSocketSessionsMap.get(groupName).remove(session);
//        //日志记录，便于调试
//        log.warn(status.toString());
//    }
//
//
//    @Override
//    public void publish(String groupName, WebSocketMessage<?> message) {
//        //判断这个比赛是否存在
//        if(webSocketSessionsMap.get(groupName) != null){
//            //这里表示在比赛广播消息的时候是没办法同时增加会话进来的
//            synchronized(webSocketSessionsMap.get(groupName)){
//                final Iterator<WebSocketSession> iterator = webSocketSessionsMap.get(groupName).iterator();
//                //迭代发送消息
//                while(iterator.hasNext()){
//                    final WebSocketSession webSocketSession = iterator.next();
//                    //判断该会话是否还是开启的
//                    if(webSocketSession.isOpen()){
//                        try{
//                            //向该回话发送消息
//                            webSocketSession.sendMessage(message);
//                        }catch(IOException e){
//                            log.warn(groupName + "有一个websocket会话连接断开" + e.getMessage());
//                            //移除这个出现异常的会话
//                            iterator.remove();
//                        }
//                    }else {
//                        //移除这个已经关闭的会话
//                        iterator.remove();
//                        log.warn(groupName + "移除一个已经断开了连接的websocket");
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * @param serverHttpRequest
//     * @param serverHttpResponse
//     * @param webSocketHandler
//     * @param map
//     * @description 链接拦截器，拦截需要的请求参数
//     * @return
//     * @throws Exception
//     */
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> map) throws Exception {
//
//        log.info("试图建立连接");
//        try {
//            //转换获取HttpServletRequest对象来获取请求参数
//            final HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();
//            //获取指定的请求参数
//            final String groupName = String.valueOf(httpServletRequest.getParameter(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME));
//            //比赛校验是否存在
//            if (!userService.isExistGameGroup(groupName) && groupName!=null && !groupName.equals("NoGroup")){
//                //获取响应对象
//                final HttpServletResponse servletResponse = ((ServletServerHttpResponse) serverHttpResponse).getServletResponse();
//                final Result responseMessage = ResultUtil.error("比赛分组不存在");
//                //设置必要响应头并进行数据响应
//                servletResponse.setContentType("application/json;charset=UTF-8");
//                servletResponse.getWriter().print(responseMessage);
//                servletResponse.flushBuffer();
//                return false;
//            }
//
//            map.put(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME,groupName);
//
//            log.info(groupName + "连接创建成功 (beforeHandshake)");
//            return true;
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        log.info("连接创建失败");
//        return false;
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
//
//    }
//}
