package com.cqupt.mislab.erpn.dao.order;

import com.cqupt.mislab.erpn.model.entity.order.AllOrdersOfGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.dao
 * @description:
 * @author: keji0
 * @date: 2021/1/19 14:59
 */
@Repository
@Mapper
public interface AllOrdersOfGroupDao {

    void addAllOrdersOfGroups(ArrayList<AllOrdersOfGroup> allOrdersOfGroups);

    List<AllOrdersOfGroup> findAllOrdersOfGroup(String groupName);

    boolean updateSpecalRems(List<AllOrdersOfGroup> allOrdersOfGroups);

    List<AllOrdersOfGroup> findOrderOfGroupByUserUnique(String userUnique);

    AllOrdersOfGroup loadAllOrderOfGroupByOrderId(@Param("orderId") int orderId);

    int findOrderStatusByOrderId(@Param("orderId") int orderId);
}
