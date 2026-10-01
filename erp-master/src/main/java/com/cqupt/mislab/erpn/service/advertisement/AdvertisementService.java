package com.cqupt.mislab.erpn.service.advertisement;

import com.cqupt.mislab.erpn.model.Result;

import javax.servlet.http.HttpSession;

/**
 * @author 张烈文
 */
public interface AdvertisementService {

    /**
     * 根据用户唯一标识查询已经开拓的市场
     * @param userUnique
     * @return
     */
    Result findDevelopedMarketByUserUnique(String userUnique);

    /**
     * 根据市场名称获取广告列表
     * @param marketName
     * @param session
     * @return
     */
    Result findAdvertisementByMarketNameAndUserUnique(String marketName, HttpSession session);


    /**
     * 投放广告给某个产品
     * @param adId
     * @param money
     * @return
     */
    Result updateAdvertisementForProduct(Integer adId, Double money);


    /**
     * 获取已经投放的广告列表
     * @param userUnique
     * @return
     */
    Result findAlreadyAdvertisement(String userUnique);


    /**
     *确认投放广告
     *
     * 0-投放广告成功
     * 1-投放广告失败（投放的钱太多）
     * 2-已经进行过结束广告费投放操作
     *
     * @param userUnique
     * @return
     */
    Result updateAdvertisementFinish(String userUnique);


    /**
     * 对于一个bug的补丁方案：当用户在前一期破产了，下一年初已经投放广告并且在等待大厅的用户没有了订单
     * bug描述: 假设有甲乙两个用户，甲操作速度很快，快速完成了第一年的企业经营，进入了第二年并完成了投广告费，来到等待大厅，准备选单。
     * 此时，乙仍然在进行第一年的企业经营，此时，若乙由于经营不少破产。甲会跳转到选单的界面，但是并没有订单供甲选择。
     * <p>
     * 产生bug的原因：出现上述Bug的原因是，当组内某个人破产后，组内其他人刚好处于投完广告费等待选单时，由于破产后，没有更新组内的选单表（Chooseorder）中数据，
     * 所以由于表（Chooseorder）中没有数据，从而造成其他人虽然可以选单，但实际上没有单可以选。
     *
     * @param userUnique
     * @author 杨青周军
     * @since 2017/03/08
     */
    boolean checkGenerateOrderIfNecessary(String userUnique);


    /**
     * 年初初始化用户需要投放的广告费的条目
     * @param userUnique
     */
    void initAdOfUser(String userUnique);
}
