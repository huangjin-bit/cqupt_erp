//package com.cqupt.mislab.erpn.commons.websocket;
//
//import javax.servlet.http.HttpSession;
//import javax.websocket.HandshakeResponse;
//import javax.websocket.server.HandshakeRequest;
//import javax.websocket.server.ServerEndpointConfig;
//
///**
// * @program: com.cqupt.mislab.erpn.commons.websocket
// * @description:
// * @author: keji0
// * @date: 2020/8/23 15:59
// */
//
//public class GetHTTPSessionConfig extends ServerEndpointConfig.Configurator {
//
//    @Override
//    public void modifyHandshake(ServerEndpointConfig sec,HandshakeRequest request, HandshakeResponse response) {
//
//        HttpSession httpSession=(HttpSession) request.getHttpSession();
//        sec.getUserProperties().put(HttpSession.class.getName(),httpSession);
//    }
//
//}
