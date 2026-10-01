package com.cqupt.mislab.erpn.commons.utils;

/**
 * @Author xhx
 * @Date 2021/9/15 17:26
 */
public class AdOfStatusUtils {
    /**计算对应adofstatus里面的时期
     * @author zhoujun
     */
    public static  int computePeriod(int currentPeriod,int years,int periodsOfOneYear){
        for(int i=1;i<=years;i++){
            if(currentPeriod==1){
                break;
            }
            if(currentPeriod<1+(i-1)*periodsOfOneYear){
                currentPeriod=1+(i-2)*periodsOfOneYear;
                break;
            }
            if(currentPeriod>(years-1)*periodsOfOneYear){
                currentPeriod=1+(years-1)*periodsOfOneYear;
                break;
            }
        }
        return currentPeriod;
    }
}
