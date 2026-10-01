package com.cqupt.mislab.erpn.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.dto
 * @description:
 * @author: keji0
 * @date: 2020/8/10 8:47
 */
@Data
@NoArgsConstructor
@ToString
@Slf4j
public class GroupCreateDto {

    private String groupName;
    private Integer maxEnterpriseNumber;
    private String teacherId;

}
