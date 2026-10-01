package com.cqupt.mislab.erpn.dao.finance.will;

import com.cqupt.mislab.erpn.model.entity.finance.will.WillReceive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 张烈文
 * @date 2021/8/9 21:19
 */
@Mapper
@Repository
public interface WillReceiveDao {
    /**
     * 新增应收账款
     * @param receive
     */
    void insertWillReceive(WillReceive receive);

    /**
     * 查询应收账款
     * @param userUnique
     * @return
     */
    List<WillReceive> listWillReceive(String userUnique);

    /**
     * 获取应收账款的money
     * @param userUnique
     * @param willReceiveId
     * @return
     */
    Double getMoney(@Param("userUnique") String userUnique, @Param("willReceiveId") Integer willReceiveId);

    /**
     * 删除指定的应收账单
     * @param userUnique
     * @param willReceiveId
     */
    void deleteWillReceive(@Param("userUnique") String userUnique, @Param("willReceiveId") Integer willReceiveId);
}
