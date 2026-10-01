package com.cqupt.mislab.erpn.service.order;

import com.cqupt.mislab.erpn.model.entity.order.AllOrdersOfGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2021/1/19 14:56
 */
public interface AllOrdersOfGroupService {
    void addAllOrdersOfGroups(ArrayList<AllOrdersOfGroup> allOrdersOfGroups);

    List<AllOrdersOfGroup> findAllOrdersOfGroup(String groupName);

    boolean updateSpecalRems(List<AllOrdersOfGroup> allOrdersOfGroups);

    List<AllOrdersOfGroup> findOrderOfGroupByUserUnique(String userUnique);

    AllOrdersOfGroup findAllOrdersOfGroupByOrderId(String orderId);

    int findOrderStatusByOrderId(String orderId);

    boolean updateOrderStatusByOrderId(String orderId,Integer status);
}
