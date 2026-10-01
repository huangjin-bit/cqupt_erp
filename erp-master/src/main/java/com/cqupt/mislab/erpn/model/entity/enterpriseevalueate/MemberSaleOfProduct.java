package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;

/**
 * @Author xhx
 * @Date 2021/11/14 15:36
 */
public class MemberSaleOfProduct {
    private GameGroupMemberDTO member;
    private String productName;
    private double sale;

    public GameGroupMemberDTO getMember() {
        return member;
    }

    public void setMember(GameGroupMemberDTO member) {
        this.member = member;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }
}
