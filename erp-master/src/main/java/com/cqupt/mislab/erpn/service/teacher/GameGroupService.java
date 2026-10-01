package com.cqupt.mislab.erpn.service.teacher;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import com.cqupt.mislab.erpn.model.vo.teacher.GameGroupMemberStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xhx
 * @Date 2021/9/8 13:03
 */
@Service
public interface GameGroupService {

    List<GameGroupInfo> showGameGroupsByTeacher(String adminId);

    List<RegisterUser> showGameGroupList(String groupName);

    List<RegisterUser> findGameGroupUserMessageByGroupName(String groupName);

    List<GameGroupMemberStatus> findGameGroupMemberStatusByGroupName(String groupName);

    /**service
     * addHistory 将游戏组的信息转入到历史数据
     * @param groupName
     *void
     */
    boolean addHistory(String groupName);

    /**
     *
     * deleteGameGroupByGroupName 删除游戏组
     *
     * @param groupName
     * @return boolean
     * @exception
     * @since 1.0.0
     */
    boolean deleteGameGroupByGroupName(String groupName);

    /**
     * 取出游戏年数
     *
     * @author LX
     * @param groupName
     * @return
     */
    int findYearsByGroupName(String groupName);

    /**
     * 取出实际参赛人数
     *
     * @author yqh
     * @param groupName
     * @return
     */
    int findMembersOfInGroupByGroupName(String groupName);

    /**
     * 判断该用户当前周期是否是年初？
     *
     * @author LX
     */
    boolean isYearBegin(String userUnique);

    /**
     * 拿出某一老师的所有游戏组
     *
     * @author zjs
     */
    List<GameGroupInfo> showGameGroups(String adminID);

    boolean addGameGroup(GameGroupInfo gameGoupInfo);

}
