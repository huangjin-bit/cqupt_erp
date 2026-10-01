package com.cqupt.mislab.erpn.service.gameGroup;

import com.cqupt.mislab.erpn.model.Result;
import org.springframework.data.repository.query.Param;

import javax.servlet.http.HttpSession;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2020/8/16 2:08
 */
public interface GroupMemberService {

    Result joinGroup(@Param("userId")String userId, @Param("groupName")String groupName,HttpSession session);

    Boolean addJoinInGroup(@Param("userId")String userId, @Param("groupName")String groupName);

    Result quitGroup(@Param("userId") String userId, @Param("groupName") String groupName, HttpSession session);

    Boolean memberQuitGroup(@Param("userId")String userId, @Param("groupName")String groupName);

    Boolean updateUserUnqiqueAndCurrentPeriod(String groupName);

    Boolean kickMember(String masterId,String influencedId,String groupName,HttpSession session);

    /**
     * 从session中获取用户唯一标识符userUnique，并结束该用户的经营
     * @param session
     * 用户唯一标识符
     * @return
     * 若成功，返回0；若失败，返回1
     */
    Result stopOperation(HttpSession session);

    /**
     * 查看当前用户的状态值
     * @param userUnique
     * @return
     */
    int findStatusByUserUnique(String userUnique);
}
