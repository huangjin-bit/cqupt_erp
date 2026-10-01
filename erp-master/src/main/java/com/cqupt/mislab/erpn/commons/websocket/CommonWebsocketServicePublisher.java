package com.cqupt.mislab.erpn.commons.websocket;

import org.springframework.web.socket.WebSocketMessage;

import javax.servlet.http.HttpSession;

/**
 * @program: com.cqupt.mislab.erpn.commons.websocket
 * @description:
 * @author: keji0
 * @date: 2020/8/5 2:40
 */
public interface CommonWebsocketServicePublisher {

    /**
     * 向指定的比赛群体里面广播消息
     * @param groupName
     * @param message
     */
    void publish(String groupName, WebSocketMessage<?> message);


    /**
     * 删除指定分组的WebSocket连接
     * @param groupName
     */
    void deleteGroup(String groupName, HttpSession session);


}
