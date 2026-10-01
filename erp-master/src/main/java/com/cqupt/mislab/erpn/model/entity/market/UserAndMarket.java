package com.cqupt.mislab.erpn.model.entity.market;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author whl
 * @date 2020/11/23 - 20:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@ToString
public class UserAndMarket {
    private String userUnique;
    private String marketName;
}
