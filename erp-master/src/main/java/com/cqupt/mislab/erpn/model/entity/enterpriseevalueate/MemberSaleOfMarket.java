package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;

/**
 * @Author xhx
 * @Date 2021/11/14 12:33
 */
public class MemberSaleOfMarket {
    private GameGroupMemberDTO member;
    private String marketname;
    private double sale;

    public GameGroupMemberDTO getMember() {
        return member;
    }

    public void setMember(GameGroupMemberDTO member) {
        this.member = member;
    }

    public String getMarketname() {
        return marketname;
    }

    public void setMarketname(String marketname) {
        this.marketname = marketname;
    }

    public double getSale() {
        return sale;
    }

    public void setSale(double sale) {
        this.sale = sale;
    }
}
