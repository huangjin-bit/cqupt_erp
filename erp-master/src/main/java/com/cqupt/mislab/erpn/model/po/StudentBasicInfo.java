package com.cqupt.mislab.erpn.model.po;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @program: com.cqupt.mislab.erpn.model.po
 * @description:
 * @author: keji0
 * @date: 2020/8/29 18:53
 */
@Data
@ToString
@Slf4j
@NoArgsConstructor
public class StudentBasicInfo extends GameGroupInfo {
    String userId;
    int loginStatus;

    public StudentBasicInfo (String userId,int loginStatus,GameGroupInfo gameGroupInfo){
        
    }
}
