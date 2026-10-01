package com.cqupt.mislab.erpn.model.entity.enterpriseevalueate;

import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;
import lombok.Data;

/**
 * @Author xhx
 * @Date 2021/11/5 16:03
 */
@Data
public class ProduceCapacity {
    //此member与原来的略有不同
    private GameGroupMemberDTO member;
    private double capacity;
    private int producePeriod;
}
