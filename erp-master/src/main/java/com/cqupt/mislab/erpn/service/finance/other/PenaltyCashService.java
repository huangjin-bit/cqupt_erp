package com.cqupt.mislab.erpn.service.finance.other;

/**
 * @author 张烈文
 * @date 2021/8/10 0:00
 */
public interface PenaltyCashService {
    /**
     * 用于计算违约金 = 商品数量 * 单价 * 违约金比率 * （实际交货日期 - 正常最晚交货日期）
     *
     * @param orderId
     * @return
     * @author LX
     */
    double computePenaltyCash(String orderId);
}
