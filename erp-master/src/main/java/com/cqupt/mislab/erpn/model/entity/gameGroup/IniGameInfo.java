package com.cqupt.mislab.erpn.model.entity.gameGroup;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/12/22 10:52
 */
@Data
@ToString
@Slf4j
@NoArgsConstructor
public class IniGameInfo {
    private String groupName;
    private String userID;
}
