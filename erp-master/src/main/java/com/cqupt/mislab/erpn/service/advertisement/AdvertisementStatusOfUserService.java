package com.cqupt.mislab.erpn.service.advertisement;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;

/**
 * @author whl
 * @date 2021/3/26 - 20:13
 */
public interface AdvertisementStatusOfUserService {
    /**
     * 传入用户唯一标识和当前周期，查询用户选订单的状态
     * @param userUnique
     * 用户唯一标识
     * @param currentPeriod
     * 当前周期
     * @return
     * false-表示选单尚未开始；
     * true-表示选单正在进行中
     */
    boolean findChooseOrderFlag(String userUnique, int currentPeriod);

    /**
     * 传入一个用户唯一标识，查询用户当前期选订单是否完成
     * @param userUnique
     * 用户唯一标识
     * @return
     * false-表示未完成；
     * true-表示已经完成；
     */
    boolean findFinishOrderFlag(String userUnique);





    /**
     * 通过userUnique
     * 查询当前用户的投广告及选订单的进度
     *
     * 5---用户选择订单已经结束
     * 4---用户开始选择订单
     * 3---用户处于等待大厅，等待其它组员完成投放广告
     * 2---用户已经提交了广告投放金额
     * 1---用户还未提交广告投放金额
     * @param userUnique
     * @return
     */
    Result findAdvertisementStatusOfUserByUserUnique(String userUnique);


    /**
     * 根据用户的userUnique查询出组员投广告的情况
     * @param userUnique
     * @return
     */
    Result findMembersCurProgress(String userUnique);

    void initAdStatusOfUser(String userUnique);

    boolean checkFinishOrderFlag(String userUnique);
}
