package com.cqupt.mislab.erpn.service.advertisement;

import java.util.List;

/**
 * @author whl
 * @date 2021/4/6 - 10:52
 */
public interface AdAndOrderRefreshService {

    /**
     * 当一个组员完成了投放广告，需要调用该方法通知其他组员"xxx投放广告完毕"
     * @param userUnique
     * 用户唯一标识符
     */
    void advertiseComplete(String userUnique);


    /**
     * 组内所有成员完成广告投放
     * @param userUnique
     */
    void allMembersComplete(String userUnique);

    void changeOrder(String userUnique);

    void finishOrder(String userUnique);

    void publishNextOrder(List<String> users);
}
