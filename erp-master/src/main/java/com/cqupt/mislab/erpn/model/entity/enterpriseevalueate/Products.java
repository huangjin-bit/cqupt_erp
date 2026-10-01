package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

/**
 * @Author xhx
 * @Date 2021/11/19 21:41
 */
public class Products {
    private String productname;
    private int pnumber;
    private double price;
    public String getProductname() {
        return productname;
    }
    public void setProductname(String productname) {
        this.productname = productname;
    }
    public int getPnumber() {
        return pnumber;
    }
    public void setPnumber(int pnumber) {
        this.pnumber = pnumber;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
}
