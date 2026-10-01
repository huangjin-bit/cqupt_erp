package com.cqupt.mislab.erpn.service.advertisement.impl;

import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementDao;
import com.cqupt.mislab.erpn.dao.advertisement.AdvertisementStatusOfUserDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.market.MarketDao;
import com.cqupt.mislab.erpn.dao.order.ChooseOrderDao;
import com.cqupt.mislab.erpn.dao.order.OrdersOfUserDao;
import com.cqupt.mislab.erpn.dao.product.ProductBasicDao;
import com.cqupt.mislab.erpn.model.entity.advertisement.AdvertisementStatusOfUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.entity.order.OrderSequence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AdvertisementServiceOrderGenerationTest {

    private AdvertisementServiceImpl advertisementService;

    @Mock
    private GroupManageDao groupManageDao;
    @Mock
    private AdvertisementStatusOfUserDao advertisementStatusOfUserDao;
    @Mock
    private MarketDao marketDao;
    @Mock
    private ProductBasicDao productBasicDao;
    @Mock
    private OrdersOfUserDao ordersOfUserDao;
    @Mock
    private AdvertisementDao advertisementDao;
    @Mock
    private ChooseOrderDao chooseOrderDao;

    @Before
    public void setUp() {
        advertisementService = new AdvertisementServiceImpl();
        ReflectionTestUtils.setField(advertisementService, "groupManageDao", groupManageDao);
        ReflectionTestUtils.setField(advertisementService, "advertisementStatusOfUserDao", advertisementStatusOfUserDao);
        ReflectionTestUtils.setField(advertisementService, "marketDao", marketDao);
        ReflectionTestUtils.setField(advertisementService, "productBasicDao", productBasicDao);
        ReflectionTestUtils.setField(advertisementService, "ordersOfUserDao", ordersOfUserDao);
        ReflectionTestUtils.setField(advertisementService, "advertisementDao", advertisementDao);
        ReflectionTestUtils.setField(advertisementService, "chooseOrderDao", chooseOrderDao);
    }

    @Test
    public void shouldNotSetChooseOrderFlagWhenChooseOrderGenerationFails() {
        String userUnique = "u1";
        int period = 1;
        String groupName = "g1";
        stubReadyToGenerateOrders(userUnique, period, groupName, false);
        doThrow(new RuntimeException("insert chooseorder failed"))
                .when(chooseOrderDao).addChooseOrder(anyString(), eq(period), eq("本地市场"), eq("P1"), eq(groupName), anyInt());

        try {
            advertisementService.isCheckOtherMemberAdvertisementFinishFlag(userUnique, period);
        } catch (RuntimeException ignored) {
        }

        verify(advertisementStatusOfUserDao, never()).updateChooseOrderFlag(any(String[].class), eq(period), eq(1));
    }

    @Test
    public void shouldRepairMissingChooseOrdersWhenChooseOrderFlagWasAlreadySet() {
        String userUnique = "u1";
        int period = 1;
        String groupName = "g1";
        stubReadyToGenerateOrders(userUnique, period, groupName, true);

        advertisementService.checkGenerateOrderIfNecessary(userUnique);

        verify(chooseOrderDao, times(3)).addChooseOrder(anyString(), eq(period), eq("本地市场"), eq("P1"), eq(groupName), anyInt());
        verify(chooseOrderDao).addChooseOrder(eq("u3"), eq(period), eq("本地市场"), eq("P1"), eq(groupName), anyInt());
        verify(advertisementStatusOfUserDao).updateChooseOrderFlag(any(String[].class), eq(period), eq(1));
    }

    @Test
    public void shouldNotDuplicateChooseOrdersWhenTheyAlreadyExist() {
        String userUnique = "u1";
        int period = 1;
        String groupName = "g1";
        stubReadyToGenerateOrders(userUnique, period, groupName, false);
        when(chooseOrderDao.countChooseOrderByNaturalKey(anyString(), eq(period), eq("本地市场"), eq("P1"), eq(groupName))).thenReturn(1);

        advertisementService.isCheckOtherMemberAdvertisementFinishFlag(userUnique, period);

        verify(chooseOrderDao, never()).addChooseOrder(anyString(), anyInt(), anyString(), anyString(), anyString(), anyInt());
        verify(advertisementStatusOfUserDao).updateChooseOrderFlag(any(String[].class), eq(period), eq(1));
    }

    private void stubReadyToGenerateOrders(String userUnique, int period, String groupName, boolean chooseOrderFlagAlreadySet) {
        when(groupManageDao.findGameGroupMemberListByStatus(1, userUnique))
                .thenReturn(Arrays.asList(member("u1", "s1", period), member("u2", "s2", period), member("u3", "s3", period)));
        when(advertisementStatusOfUserDao.findAdvertisementStatusOfUserByUserUniques(any(String[].class), eq(period)))
                .thenReturn(Arrays.asList(adStatus("u1", period, chooseOrderFlagAlreadySet), adStatus("u2", period, chooseOrderFlagAlreadySet), adStatus("u3", period, chooseOrderFlagAlreadySet)));
        when(marketDao.findMarketName()).thenReturn(Collections.singletonList("本地市场"));
        when(productBasicDao.findAllProductName()).thenReturn(Collections.singletonList("P1"));
        when(groupManageDao.findGameGroupInfoByUserUnique(userUnique)).thenReturn(group(groupName, period, 4));
        when(ordersOfUserDao.findOrdersMoney(anyString(), anyString(), anyInt(), anyInt(), eq(groupName)))
                .thenReturn(Collections.emptyList());
        when(ordersOfUserDao.findUnOrdersMoney(anyString(), anyString(), anyInt(), anyInt(), eq(groupName)))
                .thenReturn(Collections.emptyList());
        when(ordersOfUserDao.findOrdersMoneyOfUser(anyString(), anyString(), anyInt(), anyInt(), eq(groupName)))
                .thenReturn(Collections.emptyList());
        when(ordersOfUserDao.findUnOrdersMoneyOfUser(anyString(), anyString(), anyInt(), anyInt(), eq(groupName)))
                .thenReturn(Collections.emptyList());
        List<OrderSequence> adSequence = Arrays.asList(order("u1", 10.0), order("u2", 5.0), order("u3", 0.0));
        when(advertisementDao.findAdvertisementMoneyOfUsers(groupName, period, "本地市场", "P1")).thenReturn(adSequence);
    }

    private GameGroupMemberInfo member(String userUnique, String userId, int period) {
        GameGroupMemberInfo member = new GameGroupMemberInfo();
        member.setUserUnique(userUnique);
        member.setUserId(userId);
        member.setCurrentPeriod(period);
        return member;
    }

    private AdvertisementStatusOfUser adStatus(String userUnique, int period, boolean chooseOrderFlagAlreadySet) {
        AdvertisementStatusOfUser status = new AdvertisementStatusOfUser();
        status.setUserUnique(userUnique);
        status.setPeriod(period);
        status.setFinishAdvertiseFlag(1);
        status.setChooseOrderFlag(chooseOrderFlagAlreadySet ? 1 : 0);
        status.setFinishOrderFlag(0);
        return status;
    }

    private GameGroupInfo group(String groupName, int period, int periodsOfOneYear) {
        GameGroupInfo group = new GameGroupInfo();
        group.setGroupName(groupName);
        group.setCurrentPeriod(period);
        group.setPeriodsOfOneYear(periodsOfOneYear);
        return group;
    }

    private OrderSequence order(String userUnique, double money) {
        OrderSequence order = new OrderSequence();
        order.setUserUnique(userUnique);
        order.setMoney(money);
        return order;
    }
}
