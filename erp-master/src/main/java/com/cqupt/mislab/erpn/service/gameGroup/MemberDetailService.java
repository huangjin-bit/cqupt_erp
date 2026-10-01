package com.cqupt.mislab.erpn.service.gameGroup;

import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.vo.gamegroup.GameGroupMemberDetailVo;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/10/7 14:02
 */
public interface MemberDetailService {

    boolean confirmContribution(String groupName);

    GameGroupMemberDetail findMemberDetailByStudentNoUserUnique(String userUnique, String studentNo);

    List<GameGroupMemberDetail> listMemberDetailsUnordered(String userUnique);

    /**
     * 有顺序获取小组成员信息
     * @param userUnique
     * @return
     */
    Result listMemberDetails(String userUnique);

    /**
     *  添加小组成员信息
     * @param userUnique
     * @param groupMemberDetailVos
     * @return
     */
    Result savaGameGroupMemberDetails(String userUnique,List<GameGroupMemberDetailVo> groupMemberDetailVos);
}
