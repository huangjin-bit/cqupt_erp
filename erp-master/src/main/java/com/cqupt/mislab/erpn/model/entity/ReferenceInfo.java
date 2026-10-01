package com.cqupt.mislab.erpn.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/7/28 1:58
 */
@Slf4j
@Data
@NoArgsConstructor
@ToString
public class ReferenceInfo {
    private String userID;
    private String adminID;
}
