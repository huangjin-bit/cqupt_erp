package com.cqupt.mislab.erpn.model.vo.finance;

import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/9/8 22:00
 */
@Data
public class BalanceSheetVo {

    private String type1;//类型表示，资产or负债
    private float cvalue;//type1期末的总值

}
