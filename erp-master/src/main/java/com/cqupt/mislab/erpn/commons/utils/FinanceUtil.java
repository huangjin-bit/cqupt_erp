package com.cqupt.mislab.erpn.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author whl
 * @date 2021/2/3 - 0:01
 */
public class FinanceUtil {
    /**
     * 处理数字的四舍五入方法
     * @author lx hhy
     * @return
     */
    public static double round(double d) {
        BigDecimal d2 = new BigDecimal(d);
        return d2.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static float round(float d) {
        BigDecimal d2 = new BigDecimal(d);
        return d2.setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }
}
