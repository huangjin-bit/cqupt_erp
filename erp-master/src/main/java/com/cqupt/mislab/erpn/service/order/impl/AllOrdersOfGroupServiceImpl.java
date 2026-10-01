package com.cqupt.mislab.erpn.service.order.impl;

import com.cqupt.mislab.erpn.dao.order.AllOrdersOfGroupDao;
import com.cqupt.mislab.erpn.dao.order.ChooseOrderDao;
import com.cqupt.mislab.erpn.model.entity.order.AllOrdersOfGroup;
import com.cqupt.mislab.erpn.service.order.AllOrdersOfGroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: com.cqupt.mislab.erpn.service.impl
 * @description:
 * @author: keji0
 * @date: 2021/1/19 14:57
 */
@Service
@Slf4j
public class AllOrdersOfGroupServiceImpl implements AllOrdersOfGroupService {

    @Autowired
    AllOrdersOfGroupDao allOrdersOfGroupDao;
    @Autowired
    ChooseOrderDao chooseOrderDao;

    @Override
    public void addAllOrdersOfGroups(ArrayList<AllOrdersOfGroup> allOrdersOfGroups) {
        allOrdersOfGroupDao.addAllOrdersOfGroups(allOrdersOfGroups);
    }

    @Override
    public List<AllOrdersOfGroup> findAllOrdersOfGroup(String groupName) {
        return allOrdersOfGroupDao.findAllOrdersOfGroup(groupName);
    }

    @Override
    public boolean updateSpecalRems(List<AllOrdersOfGroup> allOrdersOfGroups) {
        allOrdersOfGroupDao.updateSpecalRems(allOrdersOfGroups);
        return true;
    }

    @Override
    public List<AllOrdersOfGroup> findOrderOfGroupByUserUnique(String userUnique) {
        return allOrdersOfGroupDao.findOrderOfGroupByUserUnique(userUnique);
    }

    @Override
    public AllOrdersOfGroup findAllOrdersOfGroupByOrderId(String orderId) {

        return allOrdersOfGroupDao.loadAllOrderOfGroupByOrderId(Integer.parseInt(orderId));
    }

    @Override
    public int findOrderStatusByOrderId(String orderId) {

        Integer temp = allOrdersOfGroupDao.findOrderStatusByOrderId(Integer.parseInt(orderId));
        if(temp == null){
            return -1;
        }
        return temp;
    }

    @Override
    public boolean updateOrderStatusByOrderId(String orderId, Integer status) {
        try {
            chooseOrderDao.updateChooseOrderValueByOrderId(Integer.parseInt(orderId), status);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
