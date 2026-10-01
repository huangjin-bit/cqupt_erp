package com.cqupt.mislab.erpn.model.vo.gamegroup;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import lombok.Data;

/**
 * @author 张烈文
 * @date 2021/10/25 21:11
 */
@Data
public class GameGroupMemberDetailVo implements Comparable<GameGroupMemberDetailVo> {

    private String oldStudentNo;
    private String studentNo;
    private String studentName;
    private String title;
    private Integer sort;

    @Override
    public int compareTo(GameGroupMemberDetailVo o) {
        return this.sort - o.getSort();
    }
}
