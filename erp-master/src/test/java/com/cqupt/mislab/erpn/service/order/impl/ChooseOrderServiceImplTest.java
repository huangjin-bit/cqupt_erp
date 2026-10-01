package com.cqupt.mislab.erpn.service.order.impl;

import com.cqupt.mislab.erpn.dao.order.OrdersOfUserDao;
import com.cqupt.mislab.erpn.service.order.AllOrdersOfGroupService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChooseOrderServiceImplTest {

    private ChooseOrderServiceImpl chooseOrderService;

    @Mock
    private AllOrdersOfGroupService allOrdersOfGroupService;
    @Mock
    private OrdersOfUserDao ordersOfUserDao;

    @Before
    public void setUp() throws Exception {
        lockMap().clear();
        chooseOrderService = new ChooseOrderServiceImpl();
        ReflectionTestUtils.setField(chooseOrderService, "allordersOfGroupService", allOrdersOfGroupService);
        ReflectionTestUtils.setField(chooseOrderService, "ordersOfUserDao", ordersOfUserDao);
    }

    @After
    public void tearDown() throws Exception {
        lockMap().clear();
    }

    @Test
    public void updateChoosingOrderShouldRunInTransaction() throws Exception {
        Method method = ChooseOrderServiceImpl.class.getMethod("updateChoosingOrder", String.class, String.class);

        assertNotNull(method.getAnnotation(Transactional.class));
    }

    @Test
    public void updateChoosingOrderShouldKeepOrderLockForLaterRequests() throws Exception {
        String orderId = "1001";
        when(allOrdersOfGroupService.findOrderStatusByOrderId(orderId)).thenReturn(0);
        when(ordersOfUserDao.findOrderDetail(orderId)).thenReturn(null);

        assertEquals(5, chooseOrderService.updateChoosingOrder("u1", orderId));

        ConcurrentHashMap<String, ReentrantLock> lockMap = lockMap();
        assertTrue(lockMap.containsKey(orderId));
        assertTrue(lockMap.get(orderId).tryLock());
        lockMap.get(orderId).unlock();
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<String, ReentrantLock> lockMap() throws Exception {
        Field field = ChooseOrderServiceImpl.class.getDeclaredField("lockMap");
        field.setAccessible(true);
        return (ConcurrentHashMap<String, ReentrantLock>) field.get(null);
    }
}
