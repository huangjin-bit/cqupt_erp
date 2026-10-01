package com.cqupt.mislab.erpn.service.gameGroup;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2020/12/21 17:57
 */
public interface IniGameService {

    boolean iniGameByPLSQL(String groupName);

    boolean initPredictionOfGroup(String groupName);

    boolean iniGroupOrder(String groupName);

    boolean iniOrderISO(String groupName);

    boolean updateUserUnqiqueAndCurrentPeriod(String groupName);

}
