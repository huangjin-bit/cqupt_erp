package com.cqupt.mislab.erpn.model.vo.order;

/**
 * @Author xhx
 * @Date 2021/2/9 16:17
 */
public class ChooseOrderVO {
    //这里的userUnique 指的是能够选择这张单子的人。
    private String userUnique;
    private Integer chooseID;
    private Integer chooseValue;
    //下面是allOrderOfGroup
    private String orderID;
    private String productName;
    private Integer pNumber;
    private String marketName;
    private Integer needTime;
    private Integer MoneyTime;
    private Double penalPercent;
    private String specialRem;
    private String groupName;
    private Integer status;
    private Integer period;
    private Double price;
    /**
     * 加一个新的属性：userName，选单的时候可以用用户名来显示（原先是userID）
     * @author 杨青
     * @since 2016/12/13
     */
    private String userName;

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserUnique() {
        return userUnique;
    }
    public void setUserUnique(String userUnique) {
        this.userUnique = userUnique;
    }
    public Integer getChooseID() {
        return chooseID;
    }
    public void setChooseID(Integer chooseID) {
        this.chooseID = chooseID;
    }
    public Integer getChooseValue() {
        return chooseValue;
    }
    public void setChooseValue(Integer chooseValue) {
        this.chooseValue = chooseValue;
    }
    public String getOrderID() {
        return orderID;
    }
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public Integer getpNumber() {
        return pNumber;
    }
    public void setpNumber(Integer pNumber) {
        this.pNumber = pNumber;
    }
    public String getMarketName() {
        return marketName;
    }
    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }
    public Integer getNeedTime() {
        return needTime;
    }
    public void setNeedTime(Integer needTime) {
        this.needTime = needTime;
    }
    public Integer getMoneyTime() {
        return MoneyTime;
    }
    public void setMoneyTime(Integer moneyTime) {
        MoneyTime = moneyTime;
    }
    public Double getPenalPercent() {
        return penalPercent;
    }
    public void setPenalPercent(Double penalPercent) {
        this.penalPercent = penalPercent;
    }
    public String getSpecialRem() {
        return specialRem;
    }
    public void setSpecialRem(String specialRem) {
        this.specialRem = specialRem;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getPeriod() {
        return period;
    }
    public void setPeriod(Integer period) {
        this.period = period;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

}