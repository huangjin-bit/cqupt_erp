package com.cqupt.mislab.erpn.model.vo.teacher;

/**
 * @Author xhx
 * @Date 2021/9/8 13:33
 */

public class GameGroupMemberStatus {
    private String groupName;
    private String userUnique;
    private String userName;

    private Integer currentPeriod;
    private Integer status;
    private Integer finishAdFlag;
    private Integer finishOrderFlag;
    private Integer chooseOrderFlag;
    private Integer periodsOfOneYear;
    private Integer year;
    private Integer season;
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserUnique() {
        return userUnique;
    }
    public void setUserUnique(String userUnique) {
        this.userUnique = userUnique;
    }
    public Integer getCurrentPeriod() {
        return currentPeriod;
    }
    public void setCurrentPeriod(Integer currentPeriod) {
        this.currentPeriod = currentPeriod;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getFinishAdFlag() {
        return finishAdFlag;
    }
    public void setFinishAdFlag(Integer finishAdFlag) {
        this.finishAdFlag = finishAdFlag;
    }
    public Integer getFinishOrderFlag() {
        return finishOrderFlag;
    }
    public void setFinishOrderFlag(Integer finishOrderFlag) {
        this.finishOrderFlag = finishOrderFlag;
    }
    public Integer getChooseOrderFlag() {
        return chooseOrderFlag;
    }
    public void setChooseOrderFlag(Integer chooseOrderFlag) {
        this.chooseOrderFlag = chooseOrderFlag;
    }
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    public Integer getSeason() {
        return season;
    }
    public void setSeason(Integer season) {
        this.season = season;
    }
    public Integer getPeriodsOfOneYear() {
        return periodsOfOneYear;
    }
    public void setPeriodsOfOneYear(Integer periodsOfOneYear) {
        this.periodsOfOneYear = periodsOfOneYear;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
