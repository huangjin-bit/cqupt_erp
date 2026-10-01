package com.cqupt.mislab.erpn.commons.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @program: com.cqupt.mislab.erpn.commons.utils
 * @description:
 * @author: keji0
 * @date: 2021/1/19 15:47
 */
public class ERPUtils {

    /**
     * 时间转为字符串用来合成UserUnique
     *
     * @return
     * @author lx
     */
    public static String getStringDate() {
        Date data = new Date(); //date对象代表当前的系统时间(毫秒)
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");//formatter对象是用来以指定的时间格式格式化时间的
        String dateString = formatter.format(data);//format()方法是用来格式化时间的方法
        return dateString;
    }

    /**
     * 处理数字的四舍五入方法
     *
     * @return
     * @author lx hhy
     */
    public static double round(double d) {
        //System.out.println(d);
        BigDecimal d2 = new BigDecimal(d);
        //System.out.println(d2.setScale(2, RoundingMode.HALF_EVEN));
        return d2.setScale(2, RoundingMode.HALF_EVEN).doubleValue();
    }

    public static float round(float d) {
        //System.out.println(d);
        BigDecimal d2 = new BigDecimal(d);
        //System.out.println(d2.setScale(2, RoundingMode.HALF_EVEN));
        return d2.setScale(2, RoundingMode.HALF_EVEN).floatValue();
    }
}
