package com.cqupt.mislab.erpn.commons.websocket;

import com.cqupt.mislab.erpn.commons.constant.GroupManageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 
 * @date 2020/12/9 - 18:08
 */
@Slf4j
@Component
public class WebsocketEndPoint extends TextWebSocketHandler implements CommonWebsocketServicePublisher{
    /**
     * 用于存储每一个比赛里面的session数据，每一个比赛的所有的session数据被存在同一个集合里面
     * groupName -> List<WebSocketSession>
     */
//    protected final Map<String, List<WebSocketSession>> webSocketSessionsMap = new ConcurrentHashMap<>();


      protected final Map<String, CopyOnWriteArrayList<WebSocketSession>> webSocketSessionsMap = new ConcurrentHashMap<>();


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        log.info("afterConnectionClosed");
        //获取链接
        final String groupName = (String) session.getAttributes().get(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME);
        List<WebSocketSession> webSocketSessionList = webSocketSessionsMap.get(groupName);
        if (webSocketSessionList != null) {
            webSocketSessionList.remove(session);
        }
        //防止长期运行后出现这个map中存在很多空的list占用内存的情况
        if (webSocketSessionList != null && webSocketSessionList.isEmpty()) {
            webSocketSessionsMap.remove(groupName);
            log.info("{}小组的websocket会话列表已清空，已从websocketSessionsMap中移除", groupName);
        }
        log.info("从{}小组中移除当前用户的websocket会话", groupName);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        final String groupName = (String) session.getAttributes().get(GroupManageConstant.WEBSOCKET_REQUEST_ATTR_GROUP_NAME);
        log.info("组名：{}",groupName);
        //如果这个比赛还没有人连接过，那么就创建一个新的list来存储这个比赛的会话数据
        webSocketSessionsMap
        .computeIfAbsent(groupName, key -> new CopyOnWriteArrayList<>())
        .add(session);
        log.info("afterConnectionEstablished ------ 连接已建立");

    }


    @Override
    public void publish(String groupName, WebSocketMessage<?> message) {
        if (webSocketSessionsMap.get(groupName) != null){
            synchronized (webSocketSessionsMap.get(groupName)){
                List<WebSocketSession> webSocketSessionList = webSocketSessionsMap.get(groupName);
                if (webSocketSessionList == null || webSocketSessionList.isEmpty()) {
                    log.warn("{}小组的websocket会话列表为null，无法广播消息", groupName);
                    return;
                }
                for (WebSocketSession webSocketSession : webSocketSessionList) {
                    if( webSocketSession != null && webSocketSession.isOpen()){
                        try{
                            //向该会话发送消息
                            webSocketSession.sendMessage(message);
                        }catch(IOException e){
                            log.warn(groupName + "有一个websocket会话连接断开" + e.getMessage());
                            //移除这个出现异常的会话
                            try {
                                webSocketSession.close();
                            } catch (IOException ex) {
                                log.warn("关闭WebSocketSessionId={}时发生异常:{}", webSocketSession.getId(), ex.getMessage());
                            }
                            webSocketSessionList.remove(webSocketSession);
                        }
                    }else {
                        //移除这个已经关闭的会话
                        webSocketSessionList.remove(webSocketSession);
                        log.warn(groupName + "移除一个已经断开了连接的websocket");
                    }
                }
            }
        }
    }

//  老的publish方法的逻辑
//  不清楚为何要用迭代器这么复杂的形式，我使用foreach循环来完成相同的功能，以提高代码可读性，但是可能有bug
//  出现bug时应该首先使用老的实现方法替代，不行的话再考虑
//*********************************************************************************************
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

    @Override
    public void deleteGroup(String groupName, HttpSession session) {
        List<WebSocketSession> webSocketSessionList = webSocketSessionsMap.get(groupName);
        //防止空指针异常
        if(webSocketSessionList == null) {
            return;
        }
        for (WebSocketSession webSocketSession:webSocketSessionList) {
            try {
                if (webSocketSession != null && webSocketSession.isOpen()  ) {
                    webSocketSession.close();
                    log.info("WebSocketSessionId={}已断开",webSocketSession.getId());
                }
            } catch (IOException e) {
                log.warn("关闭WebSocketSessionId={}时发生异常:{}", webSocketSession.getId(), e.getMessage());
            }
        }
        webSocketSessionsMap.remove(groupName);
        log.info("{}小组的websocket会话列表已清空，已从websocketSessionsMap中移除", groupName);
    }

}
