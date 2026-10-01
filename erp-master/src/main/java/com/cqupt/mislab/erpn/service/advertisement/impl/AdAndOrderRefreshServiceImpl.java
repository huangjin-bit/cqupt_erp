package com.cqupt.mislab.erpn.service.advertisement.impl;

import com.alibaba.fastjson.JSONObject;
import com.cqupt.mislab.erpn.commons.constant.WsResultEnum;
import com.cqupt.mislab.erpn.commons.websocket.WebsocketEndPoint;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.service.advertisement.AdAndOrderRefreshService;
import com.cqupt.mislab.erpn.commons.utils.WsResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.List;

/**
 * @author whl
 * @date 2021/4/6 - 10:52
 */
@Service
@Slf4j
public class AdAndOrderRefreshServiceImpl implements AdAndOrderRefreshService {

    @Autowired
    CommonDao commonDao;

    @Autowired
    WebsocketEndPoint websocketEndPoint;

    /**
     * 当一个组员完成了投放广告，需要调用该方法通知其他组员"xxx投放广告完毕"
     * @param userUnique 用户唯一标识符
     */
    @Override
    public void advertiseComplete(String userUnique) {
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        String userId = commonDao.findUserIdByUserUnique(userUnique);
        String userName = commonDao.findUserNameByUserId(userId);

        log.info("userUnique:{},userId:{},userName:{}",userUnique,userId,userName);
        String message = JSONObject.toJSONString(WsResultUtil.completeAdvertise(userName));
        log.info("组内广播: {}", message);
        websocketEndPoint.publish(groupName, new TextMessage(message));
    }

    @Override
    public void allMembersComplete(String userUnique) {
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        String message = JSONObject.toJSONString(WsResultUtil.allMembersCompleteAdvertise("所有组员投放广告完毕"));
        log.info("组内广播: {}", message);
        websocketEndPoint.publish(groupName, new TextMessage(message));
    }

    @Override
    public void changeOrder(String userUnique) {
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        String message = JSONObject.toJSONString(WsResultUtil.orderChosenResponse(WsResultEnum.ORDER_CHOSEN.getMessage()));
        websocketEndPoint.publish(groupName, new TextMessage(message));
    }

    @Override
    public void finishOrder(String userUnique) {
        String groupName = commonDao.findGameGroupNameByUserUnique(userUnique);
        String message = JSONObject.toJSONString(WsResultUtil.orderChosenResponse(WsResultEnum.ORDER_CHOSEN.getMessage()));
        websocketEndPoint.publish(groupName, new TextMessage(message));
    }

    @Override
    public void publishNextOrder(List<String> users) {
        StringBuffer all = new StringBuffer();
        String groupName = commonDao.findGameGroupNameByUserUnique(users.get(0));
        for(String user:users){
            all.append(user+"、");
        }
        all.deleteCharAt(all.length()-1);
        websocketEndPoint.publish(groupName, new TextMessage(all.toString()));
    }
}
