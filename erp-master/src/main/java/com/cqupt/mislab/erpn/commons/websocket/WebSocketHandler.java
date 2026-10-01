//package com.cqupt.mislab.erpn.commons.websocket;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * @program: com.cqupt.mislab.erpn.commons.websocket
// * @description:
// * @author: keji0
// * @date: 2020/8/23 17:38
// */
//
//public class WebSocketHandler extends TextWebSocketHandler {
//
//    private final static Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
//
//    //储存分组信息 分组名:用户分组list
//    private static final Map<String, ArrayList<WebSocketSession>> userMap = new HashMap<>();
//
//    //实例化的类
//    public static WebSocketHandler webSocketHandler;
//    /**
//     * 实例化方法
//     * @return webSocketHandler
//     */
//    public static WebSocketHandler getBeans(){
//        if(webSocketHandler == null){
//            webSocketHandler = new WebSocketHandler();
//        }
//        return webSocketHandler;
//    }
//
//    /**
//     * 处理前端发送的文本信息
//     * js调用websocket.send时候，会调用该方法
//     * @param session session
//     * @param message 发送信息
//     * @throws Exception 异常
//     */
//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        //获取用户id
//        String userId = (String) session.getAttributes().get("userId");
//        //用户分组
//        String usergroup = (String) session.getAttributes().get("groupName");
//        //消息
//        AppUserBo appUserBo =  new AppUserBo(userId,usergroup,message.getPayload());
//        // 获取提交过来的消息详情
//        logger.debug("收到用户 " + userId + "的消息:" + message);
//        if(ToolsUtil.isNullOrEmpty(message.getPayload())){
//            //回复一条信息
//            if(ToolsUtil.isNotNull(usergroup)){
//                sendMessageToGroup(usergroup,appUserBo);
//            }else{
//                session.sendMessage(sendJsonMsg(appUserBo));
//            }
//            return;
//        }
//        if("{".equals(message.getPayload().substring(0,1))){
//            Map<String,Object> map = new HashMap<>(1);
//            try {
//                map = JSONObject.fromObject(message.getPayload());
//            } catch (Exception e) {
//                //回复一条信息，
//                if(ToolsUtil.isNotNull(usergroup)){
//                    sendMessageToGroup(usergroup,appUserBo);
//                }else{
//                    session.sendMessage(sendJsonMsg(appUserBo));
//                }
//            }
//            String methond = (String)map.get("methond");
//        }else if("[".equals(message.getPayload().substring(0,1))){
//            JSONArray jsonArray = null;
//            try {
//                jsonArray = JSONArray.fromObject(message.getPayload());
//            } catch (Exception e) {
//                //回复一条信息，
//                if(ToolsUtil.isNotNull(usergroup)){
//                    sendMessageToGroup(usergroup,appUserBo);
//                }else{
//                    session.sendMessage(sendJsonMsg(appUserBo));
//                }
//            }
//        }else{
//            //回复一条信息，
//            if(ToolsUtil.isNotNull(usergroup)){
//                sendMessageToGroup(usergroup,appUserBo);
//            }else{
//                session.sendMessage(sendJsonMsg(appUserBo));
//            }
//        }
//
//    }
//
//
//    /**
//     * 当新连接建立的时候，被调用
//     * 连接成功时候，会触发页面上onOpen方法
//     * @param session session
//     * @throws Exception 异常
//     */
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//        //已建立连接的用户
//        ArrayList<WebSocketSession> users = new ArrayList<>();
//        users.add(session);
//        // 这块会实现自己业务，比如，当用户登录后，会把离线消息推送给用户
//        String userId = (String) session.getAttributes().get("WEBSOCKET_ID");
//        //已建立的用户分组
//        String usergroup = (String) session.getAttributes().get("WEBSOCKET_GROUP");
//        //昵称
//        String nickName = (String) session.getAttributes().get("WEBSOCKET_NICK_NAME");
//        if(usergroup==null){
//            userMap.put("",users);
//        }else{
//            userMap.put(usergroup,users);
//        }
//        logger.info(usergroup+"组下用户:" + userId +",昵称:"+nickName+ " 连接成功");
//        session.sendMessage(sendJsonMsg(session,"连接成功"));
//        //session.sendMessage(sendJsonMsg(session,"欢迎萌新~"));
//    }
//
//    /**
//     * 当连接关闭时被调用
//     * @param session session
//     * @param status 连接状态
//     * @throws Exception 异常
//     */
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        String username = (String) session.getAttributes().get("WEBSOCKET_ID");
//        //已建立的用户分组
//        String usergroup = (String) session.getAttributes().get("WEBSOCKET_GROUP");
//        ArrayList<WebSocketSession> userList  = userMap.get(usergroup);
//        logger.info(usergroup+"组下用户 " + username + " 连接关闭. 状态: " + status);
//        userList.remove(session);
//        //当该组成员为空的时候，删除该组
//        if(userList.size()<=0){
//            userMap.remove(usergroup);
//        }
//    }
//
//    /**
//     * 传输错误时调用
//     * @param session session
//     * @param exception 异常
//     * @throws Exception 异常
//     */
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
//        String username = (String) session.getAttributes().get("WEBSOCKET_ID");
//        if (session.isOpen()) {
//            session.close();
//        }
//        //已建立的用户分组
//        String usergroup = (String) session.getAttributes().get("WEBSOCKET_GROUP");
//        ArrayList<WebSocketSession> userList  = userMap.get(usergroup);
//        logger.debug(usergroup+"组下用户 " + username +" websocket 连接传输错误......");
//        userList.remove(session);
//    }
//
//    /**
//     * 给所有在线用户发送消息
//     * @param message 发送消息
//     */
//    public void sendMessageToUsers(String message) {
//        //遍历组map
//        Set<String> set = userMap.keySet();
//        for (String key:set) {
//            ArrayList<WebSocketSession> users = userMap.get(key);
//            //遍历组下成员
//            for (WebSocketSession user : users) {
//                try {
//                    if (user.isOpen()) {
//                        user.sendMessage(sendJsonMsg(message,null,null));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 给某个用户发送消息
//     * @param userId 用户id
//     * @param message 发送信息
//     */
//    public void sendMessageToUser(String userId,String message) {
//        //遍历组map
//        Set<String> set = userMap.keySet();
//        for (String key:set) {
//            ArrayList<WebSocketSession> users = userMap.get(key);
//            //遍历组下成员
//            for (WebSocketSession user : users) {
//                if (user.getAttributes().get("WEBSOCKET_ID").equals(userId)) {
//                    try {
//                        if (user.isOpen()) {
//                            user.sendMessage(sendJsonMsg(message,userId,null));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//    }
//
//    /**
//     * 给某个组下所有用户发送消息
//     * @param groupName 分组
//     * @param message 消息
//     */
//    public void sendMessageToGroup(String groupName,String message) {
//        if(groupName!=null){
//            //取出该分组名下的用户列表
//            ArrayList<WebSocketSession> userList = userMap.get(groupName);
//            if(userList!=null&&userList.size()>0){
//                for(WebSocketSession user:userList){
//                    if (user.isOpen()) {
//                        try {
//                            user.sendMessage(sendJsonMsg(message,null,groupName));
//                        } catch (IOException e) {
//                            logger.error("给用户"+user.getId()+"发送信息失败。");
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 获取当前用户id
//     * @param session session
//     * @return id
//     */
//    private String getClientId(WebSocketSession session) {
//        try {
//            return (String) session.getAttributes().get("WEBSOCKET_ID");
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /**
//     * 获取当前用户WebSocketSession信息
//     * @param userId 用户id
//     * @return 当前用户WebSocketSession信息
//     */
//    public WebSocketSession getUserWebSocketSession(String userId) {
//        //遍历组map
//        Set<String> set = userMap.keySet();
//        for (String key:set) {
//            ArrayList<WebSocketSession> users = userMap.get(key);
//            //遍历组下成员
//            for (WebSocketSession user : users) {
//                if (user.getAttributes().get("WEBSOCKET_ID").equals(userId)) {
//                    return user;
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 获取当前用户所在分组
//     * @param session session
//     * @return 分组名
//     */
//    private String getGroupName(WebSocketSession session) {
//        try {
//            return (String) session.getAttributes().get("WEBSOCKET_GROUP");
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /**
//     * 获取所有分组名称
//     * @return 分组名称
//     */
//    public List<String>  getAllUserGroup() {
//        //取出所有分组名称
//        Set<String> set = userMap.keySet();
//        //放入list内
//        return new ArrayList<>(set);
//    }
//
//    /***
//     * 取出该分组下用户信息
//     * @param groupName 分组名称
//     * @return 返回
//     */
//    public ArrayList<WebSocketSession> getGroupUserWebSocketSession(String groupName) {
//        ArrayList<WebSocketSession> userList = new ArrayList<>(2);
//        try {
//            if(groupName!=null){
//                userList = userMap.get(groupName);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return userList;
//    }
//
//    /**
//     * 获取所有用户的信息
//     * @return 所有用户的信息
//     */
//    public ArrayList<WebSocketSession> getAllUserWebSocket() {
//        ArrayList<WebSocketSession> allUserList = new ArrayList<>();
//        //遍历组map
//        Set<String> set = userMap.keySet();
//        for (String key:set) {
//            ArrayList<WebSocketSession> users = userMap.get(key);
//            //遍历组下成员
//            for (WebSocketSession user : users) {
//                if (user.isOpen()) {
//                    allUserList.add(user);
//                }
//            }
//        }
//        return allUserList;
//    }
//
//    /**
//     * 将消息转换为json格式发送
//     * @param session session
//     * @param msg 消息
//     * @return TextMessage类型
//     */
//    private TextMessage sendJsonMsg(WebSocketSession session,String msg){
//        String backMsg = "{}";
//        //用户id
//        String userId = (String) session.getAttributes().get("WEBSOCKET_ID");
//        //已建立的用户分组
//        String userGroup = (String) session.getAttributes().get("WEBSOCKET_GROUP");
//        //昵称
//        String nickName = (String) session.getAttributes().get("WEBSOCKET_NICK_NAME");
//        if(ToolsUtil.isNotNull(userId)&&ToolsUtil.isNotNull(userGroup)){
//            Map<String,Object> mapTmp = new HashMap<>();
//            mapTmp.put("userId",userId);
//            mapTmp.put("nick_name",nickName);
//            mapTmp.put("userGroup",userGroup);
//            mapTmp.put("msg",msg);
//            //json化
//            backMsg = JSONObject.fromObject(mapTmp).toString();
//        }
//        return new TextMessage(backMsg);
//    }
//
//    /**
//     * 将消息转换为json格式发送
//     * @param msg 消息
//     * @param userId 用户id
//     * @param userGroup 用户组
//     * @return TextMessage类型消息
//     */
//    private TextMessage sendJsonMsg(String msg,String userId,String userGroup){
//        String backMsg = null;
//        Map<String,Object> mapTmp = new HashMap<>();
//        if(ToolsUtil.isNotNull(userId)){
//            mapTmp.put("userId",userId);
//        }
//        if(ToolsUtil.isNotNull(userGroup)){
//            mapTmp.put("userGroup",userGroup);
//        }
//        mapTmp.put("msg",msg);
//        //json化
//        backMsg = JSONObject.fromObject(mapTmp).toString();
//        return new TextMessage(backMsg);
//    }
//
//    /**
//     * 将消息转换为json格式发送
//     * @param appUserBo 用户消息对象
//     * @return TextMessage类型消息
//     */
//    private TextMessage sendJsonMsg(AppUserBo appUserBo){
//        String backMsg = null;
//        String userId = appUserBo.getWebSocketId();
//        String userGroup = appUserBo.getWebSocketIdGroup();
//        String nickname = appUserBo.getNick_name();
//        Map<String,Object> mapTmp = new HashMap<>();
//        if(ToolsUtil.isNotNull(appUserBo)){
//            mapTmp.put("userId",userId);
//        }
//        if(ToolsUtil.isNotNull(userGroup)){
//            mapTmp.put("userGroup",userGroup);
//        }
//        if(ToolsUtil.isNotNull(nickname)){
//            mapTmp.put("nick_name",nickname);
//        }
//        mapTmp.put("msg",appUserBo.getSendWebSocketMsg());
//        //json化
//        backMsg = JSONObject.fromObject(mapTmp).toString();
//        return new TextMessage(backMsg);
//    }
//
//    /**
//     * 对小组发送消息
//     * @param groupName 小组名
//     * @param appUserBo app用户对象
//     */
//    public void sendMessageToGroup(String groupName,AppUserBo appUserBo) {
//        if(groupName!=null){
//            //取出该分组名下的用户列表
//            ArrayList<WebSocketSession> userList = userMap.get(groupName);
//            if(userList!=null&&userList.size()>0){
//                for(WebSocketSession user:userList){
//                    if (user.isOpen()) {
//                        try {
//                            user.sendMessage(sendJsonMsg(appUserBo));
//                        } catch (IOException e) {
//                            logger.error("给用户"+user.getId()+"发送信息失败。");
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * 给所有在线用户发送消息
//     * @param appUserBo app用户对象
//     */
//    public void sendMessageToUsers(AppUserBo appUserBo) {
//        //遍历组map
//        Set<String> set = userMap.keySet();
//        for (String key:set) {
//            ArrayList<WebSocketSession> users = userMap.get(key);
//            //遍历组下成员
//            for (WebSocketSession user : users) {
//                try {
//                    if (user.isOpen()) {
//                        user.sendMessage(sendJsonMsg(appUserBo));
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 给某个用户发送消息
//     * @param userId 用户id
//     * @param appUserBo app用户对象
//     */
//    public void sendMessageToUser(String userId,AppUserBo appUserBo) {
//        //遍历组map
//        Set<String> set = userMap.keySet();
//        for (String key:set) {
//            ArrayList<WebSocketSession> users = userMap.get(key);
//            //遍历组下成员
//            for (WebSocketSession user : users) {
//                if (user.getAttributes().get("WEBSOCKET_ID").equals(userId)) {
//                    try {
//                        if (user.isOpen()) {
//                            user.sendMessage(sendJsonMsg(appUserBo));
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    break;
//                }
//            }
//        }
//}
