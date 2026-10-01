package com.cqupt.mislab.erpn.service.finance.account;

import com.cqupt.mislab.erpn.model.Result;

/**
 * @author 张烈文
 */
public interface AccountDetailService {

    /**
     * 向向分录明细表(ACCOUNTDETAIL)表中添加两条记录
     * 当Item对应的账目为资产时，ItemType为“借”符号为“+”，贷为“-”；
     * 当Item对应的账目为负债时，ItemType为“借”符号为“-”，贷为“+”；
     * 当Item对应的账目为收入时，ItemType为“借”符号为“-”，贷为“+”；
     * 当Item对应的账目为成本时，ItemType为“借”符号为“+”，贷为“-”；
     * 当Item对应的账目为利润时，ItemType为“借”符号为“-”，贷为“+”；
     * 当Item对应的账目为所有者权益时，ItemType为“借”符号为“-”，贷为“+”；
     *
     * @param userUnique 用户在竞赛中的唯一标识
     * @param item1      科目的名称,外码，参照 ACCOUNTTWOTOONEBASIC表的TwoItem
     * @param item2      科目的名称,外码，参照 ACCOUNTTWOTOONEBASIC表的TwoItem
     * @param itemType1  科目对应的借或贷类型，其值只能是“借”或者“贷”
     * @param itemType2  科目对应的借或贷类型，其值只能是“借”或者“贷”
     * @param money      金额
     * @param calValue1  CalValue是为了方便计算“资产负债表”和“利润”表而设立的值，其数值大小与Money字段相同，但是带有符号。
     * @param calValue2  CalValue是为了方便计算“资产负债表”和“利润”表而设立的值，其数值大小与Money字段相同，但是带有符号。
     * @author 毛家杰
     */
    public void addAccountDetail(String userUnique, String item1, String item2,
                                 String itemType1, String itemType2, Double money, Double calValue1,
                                 Double calValue2);

    /**
     * 向向分录明细表(ACCOUNTDETAIL)表中添加3条记录, 注意itemType1和itemType2最好一样,以后显示在网页上会好看点,
     * 最好让calValue1,calValue2的绝对值等于calValue3的绝对值,
     * 当Item对应的账目为资产时，ItemType为“借”符号为“+”，贷为“-”；
     * 当Item对应的账目为负债时，ItemType为“借”符号为“-”，贷为“+”；
     * 当Item对应的账目为收入时，ItemType为“借”符号为“-”，贷为“+”；
     * 当Item对应的账目为成本时，ItemType为“借”符号为“+”，贷为“-”；
     * 当Item对应的账目为利润时，ItemType为“借”符号为“-”，贷为“+”；
     * 当Item对应的账目为所有者权益时，ItemType为“借”符号为“-”，贷为“+”；
     *
     * @param userUnique 用户在竞赛中的唯一标识
     * @param item1      科目的名称,外码，参照 ACCOUNTTWOTOONEBASIC表的TwoItem
     * @param item2      科目的名称,外码，参照 ACCOUNTTWOTOONEBASIC表的TwoItem
     * @param item3      科目的名称,外码，参照 ACCOUNTTWOTOONEBASIC表的TwoItem
     * @param itemType1  科目对应的借或贷类型，其值只能是“借”或者“贷”
     * @param itemType2  科目对应的借或贷类型，其值只能是“借”或者“贷”
     * @param itemType3  科目对应的借或贷类型，其值只能是“借”或者“贷”
     * @param calValue1  CalValue是为了方便计算“资产负债表”和“利润”表而设立的值，其数值大小与Money字段相同，但是带有符号。
     * @param calValue2  CalValue是为了方便计算“资产负债表”和“利润”表而设立的值，其数值大小与Money字段相同，但是带有符号。
     * @param calValue3  CalValue是为了方便计算“资产负债表”和“利润”表而设立的值，其数值大小与Money字段相同，但是带有符号。
     * @author 毛家杰
     */
    public void addAccountDetail(String userUnique, String item1, String item2,
                                 String item3, String itemType1, String itemType2, String itemType3,
                                 Double calValue1, Double calValue2, Double calValue3);

    /**
     * 查询用户会计分录信息
     * @param userUnique
     * @param pageIndex
     * @param pageSize
     * @param beginYear
     * @param beginPeriod
     * @param endYear
     * @param endPeriod
     * @return
     */
    Result findAccountInfoByUserUnique(String userUnique, Integer pageIndex, Integer pageSize, Integer beginYear, Integer beginPeriod, Integer endYear, Integer endPeriod);

}
