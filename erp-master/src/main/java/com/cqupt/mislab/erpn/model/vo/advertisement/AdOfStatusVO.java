package com.cqupt.mislab.erpn.model.vo.advertisement;

/**
 * @Author xhx
 * @Date 2021/9/15 17:28
 */
public class AdOfStatusVO {
    private int finishAdFlag;
    public int getFinishAdFlag() {
        return finishAdFlag;
    }
    public void setFinishAdFlag(int finishAdFlag) {
        this.finishAdFlag = finishAdFlag;
    }
    public int getChooseOrderFlag() {
        return chooseOrderFlag;
    }
    public void setChooseOrderFlag(int chooseOrderFlag) {
        this.chooseOrderFlag = chooseOrderFlag;
    }
    public int getFinishOrderFlag() {
        return finishOrderFlag;
    }
    public void setFinishOrderFlag(int finishOrderFlag) {
        this.finishOrderFlag = finishOrderFlag;
    }
    private int chooseOrderFlag;
    private int finishOrderFlag;
}
