package com.cqupt.mislab.erpn.model.entity.prediction;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.entity
 * @description:
 * @author: keji0
 * @date: 2020/12/22 11:27
 */
@Data
@ToString
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class PredictionOfGroup {
    private String groupName;
    private Integer period;
    private String marketName;
    private String productName;
    private Double price;
    private Integer mount;
    private Integer predictionID;
}
