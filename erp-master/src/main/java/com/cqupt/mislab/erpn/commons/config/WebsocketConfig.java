package com.cqupt.mislab.erpn.commons.config;

import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @program: com.cqupt.mislab.erpn.commons.config
 * @description:
 * @author: keji0
 * @date: 2020/8/5 2:38
 */
@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebsocketEndPoint websocketEndPoint;

    @Autowired
    private HandshakeInterceptorForWebSocket handshakeInterceptorForWebSocket;

//    @Bean
//    public ServerEndpointExporter serverEndpointExporter() {
//        return new ServerEndpointExporter();
//    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        //websocket地址 -> ws://localhost:8083/erpn/commonWebSocket?groupName=Xxx
        //注册比赛的websocket
        registry.addHandler(websocketEndPoint,"/commonWebsocket")
                .addInterceptors(handshakeInterceptorForWebSocket)
                .setAllowedOrigins("*");

    }
}
