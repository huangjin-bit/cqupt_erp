package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;

/**
 * @Author xhx
 * @Date 2021/11/19 22:09
 */
public class ProductProfit {
    private GameGroupMemberDTO member;
    private String productname;
    private String pnumber;
    private double price;
    private double cost;
    private double sale;

    public GameGroupMemberDTO getMember() {
        return member;
    }

    public void setMember(GameGroupMemberDTO member) {
        this.member = member;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }
    public String getPnumber() {
        return pnumber;
    }

    public void setPnumber(String pnumber) {
        this.pnumber = pnumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }
}
