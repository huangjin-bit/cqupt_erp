package com.cqupt.mislab.erpn.model.vo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张烈文
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoBeginGame {

    private String userName;
    private Integer years;
    private Integer currentPeriod;
    private String groupName;
    private Double tax;
    private Double cash;
    private Integer factoryCount;
    private String userUnique;
    private String gender;
}
