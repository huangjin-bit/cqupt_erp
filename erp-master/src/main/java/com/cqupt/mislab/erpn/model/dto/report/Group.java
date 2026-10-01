package com.cqupt.mislab.erpn.model.dto.report;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import lombok.Data;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/10/29 11:46
 */
@Data
public class Group {
    private String teamLeader;
    private List<GameGroupMemberDetail> members;

}
