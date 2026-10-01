package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

/**
 * @Author xhx
 * @Date 2021/11/19 20:48
 */
public class UserInputAndOutputOfAd {
    private String userunique;
    private String userID;
    private int year;
    private double input;
    private double output;

    public double getInput() {
        return input;
    }

    public double getOutput() {
        return output;
    }

    public double getRate() {
        double rate = output / input;
        if (!Double.isInfinite(rate) && !Double.isNaN(rate))
            return rate;
        return 0;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserunique() {
        return userunique;
    }

    public int getYear() {
        return year;
    }

    public void setInput(double input) {
        this.input = input;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setUserunique(String userunique) {
        this.userunique = userunique;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
