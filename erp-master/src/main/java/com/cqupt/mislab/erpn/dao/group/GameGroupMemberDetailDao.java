package com.cqupt.mislab.erpn.dao.group;

import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/10/7 13:00
 */
@Mapper
public interface GameGroupMemberDetailDao {
    boolean confirmContribution(String groupName);

    List<GameGroupMemberDetail> selectAllRolesByGameName(String gameGroupName);

    int isConfirm(String userUnique);


    List<GameGroupMemberDetail> findAllInOneTeam(String userUnique);

    GameGroupMemberDetail findMemberDetailByStudentNo_userUnique(@Param("userUnique") String userUnique, @Param("studentNo") String studentNo);

    void deleteMember(@Param("userUnique") String userUnique, @Param("removeStudentNo") String removeStudentNo);

    void insertMemberDetail(GameGroupMemberDetail groupMemberDetail);

    /**
     * 没修改成员的学号
     * @param userUnique
     * @param studentNo
     * @param studentName
     * @param title
     * @param sort
     */
    void updateMemberDetail(@Param("userUnique") String userUnique, @Param("studentNo") String studentNo
            ,@Param("studentName")String studentName
            ,@Param("title")String title, @Param("sort") Float sort);

    /**
     * 修改了成员的学号
     * @param userUnique
     * @param oldStudentNo
     * @param studentNo
     * @param title
     * @param studentName
     * @param sort
     */
    void updateMemberDetailByOldStudentNo(@Param("userUnique") String userUnique,@Param("oldStudentNo") String oldStudentNo,
                            @Param("studentNo") String studentNo, @Param("title") String title,
                            @Param("studentName") String studentName,@Param("sort") float sort);

    /**
     * 查询指定企业下的所有已经确认的成员信息
     * @param userUnique
     * @return
     */
    List<GameGroupMemberDetail> findConfirmAll(String userUnique);


    /**
     *
     * 修改gamegroupmemberdetail表中的score和grade
     * @param userUnique
     * @param studentNo
     * @param score
     * @param grade
     */
    void updateMemberDetailByGroupMemberDetail(@Param("userUnique") String userUnique, @Param("studentNo") String studentNo,
                                               @Param("score") Float score, @Param("grade") String grade);
}
