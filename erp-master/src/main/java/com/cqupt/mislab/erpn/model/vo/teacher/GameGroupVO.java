package com.cqupt.mislab.erpn.model.vo.teacher;

/**
 * @Author xhx
 * @Date 2021/9/8 13:32
 */
public class GameGroupVO {
    /**
     * 用于判断游戏是否结束时候存放
     * 用户CurrentPeriod和GameGroup.CurrentPeriod*periodsOfOneYear
     * @author lx
     */
    private int CurrentPeriod;//用户当前期数
    private int TotalPeriodOfGroup;//游戏总共的期数


    public int getCurrentPeriod() {
        return CurrentPeriod;
    }
    public void setCurrentPeriod(int currentPeriod) {
        CurrentPeriod = currentPeriod;
    }
    public int getTotalPeriodOfGroup() {
        return TotalPeriodOfGroup;
    }
    public void setTotalPeriodOfGroup(int totalPeriodOfGroup) {
        TotalPeriodOfGroup = totalPeriodOfGroup;
    }
}
