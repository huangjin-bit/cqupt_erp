package com.cqupt.mislab.erpn.model.entity.order;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author xhx
 * @Date 2021/2/1 20:53
 */
@Data
@NoArgsConstructor
public class OrderSequence {
    private String userUnique;
    private Double money;
    private Integer sequence;
}
