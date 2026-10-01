package com.cqupt.mislab.erpn.commons.utils;

import com.cqupt.mislab.erpn.commons.constant.WsResultEnum;
import com.cqupt.mislab.erpn.model.WebsocketResult;

/**
 * @author whl
 * @date 2020/12/17 - 23:37
 */
public class WsResultUtil {


    /**
     * 解散分组，所需的返回结果的封装
     * @param data
     * @return
     */
    public static WebsocketResult<Object> deleteGroup(Object data) {
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.DELETE_GROUP.getType());
        websocketResult.setMessage(WsResultEnum.DELETE_GROUP.getMessage());
        websocketResult.setData(data);
        return websocketResult;
    }

    public static WebsocketResult<Object> deleteGroup() {
        return deleteGroup(null);
    }

    public static WebsocketResult<Object> UpdateGroup(Object data){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.UPDATE_GROUP.getType());
        websocketResult.setMessage(WsResultEnum.UPDATE_GROUP.getMessage());
        websocketResult.setData(data);
        return websocketResult;
    }

    public static WebsocketResult<Object> UpdateGroup(){
        return UpdateGroup(null);
    }

    public static WebsocketResult<Object> UpdateMember(Object data){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.UPDATE_MEMBER.getType());
        websocketResult.setMessage(WsResultEnum.UPDATE_MEMBER.getMessage());
        websocketResult.setData(data);
        return websocketResult;
    }
    public static WebsocketResult<Object> kickMember(Object data,String message){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.KICK_MEMBER.getType());
        websocketResult.setMessage(message);
        websocketResult.setData(data);
        return websocketResult;
    }

    public static WebsocketResult<Object> UpdateMember(){
        return UpdateMember(null);
    }

    /**
     * 创建一个WebsocketResult对象，只在开始比赛接口中使用
     * @param data
     * 要广播的数据，通常是小组的信息
     * @param message
     * 要广播的信息：xxx开始xxx比赛
     * @return
     * Websocket专用的返回格式，通常要转换为json字符串再使用
     */
    public static WebsocketResult<Object> startGame(Object data, String message){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.START_GAME.getType());
        websocketResult.setMessage(message);
        websocketResult.setData(data);
        return websocketResult;
    }

    /**
     * 创建一个WebsocketResult对象，只在小组中某个用户投放广告完毕时使用，用于通知其它组员"此人已经投放完毕"
     * @param message
     * 要广播的消息，格式为"xxx投放广告完毕"
     * @return
     * Websocket专用的返回格式，通常要转换为json字符串再使用
     */
    public static WebsocketResult<Object> completeAdvertise(String message){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.ADVERTISE_COMPLETE.getType());
        websocketResult.setMessage(message);
        websocketResult.setData(null);
        return websocketResult;
    }


    public static WebsocketResult<Object> allMembersCompleteAdvertise(String message){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.ALLMEMBERS_COMPLETE_ADVERTISE.getType());
        websocketResult.setMessage(message);
        websocketResult.setData(null);
        return websocketResult;
    }

    public static WebsocketResult<Object> orderChosenResponse(String message){
        WebsocketResult<Object> websocketResult = new WebsocketResult<>();
        websocketResult.setType(WsResultEnum.ORDER_CHOSEN.getType());
        websocketResult.setMessage(message);
        websocketResult.setData(null);
        return websocketResult;
    }

}
