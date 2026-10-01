package com.cqupt.mislab.erpn.e2e;

import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.order.ChooseOrderDao;
import com.cqupt.mislab.erpn.dao.report.ExperienceDao;
import com.cqupt.mislab.erpn.commons.constant.ForwardStatusEnum;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.GroupCreateDto;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.model.vo.gamegroup.ForwardQuaterVo;
import com.cqupt.mislab.erpn.model.vo.order.ChooseOrderVO;
import com.cqupt.mislab.erpn.service.advertisement.AdvertisementService;
import com.cqupt.mislab.erpn.service.forwardquarter.ForwardQuaterService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupManageService;
import com.cqupt.mislab.erpn.service.gameGroup.GroupMemberService;
import com.cqupt.mislab.erpn.service.order.ChooseOrderService;
import com.cqupt.mislab.erpn.service.report.ReportService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("h2e2e")
@Sql(scripts = {"/schema-h2e2e.sql", "/data-h2e2e.sql"})
public class ErpThreeStudentBusinessChainH2Test {

    private static final String GROUP_NAME = "H2三人完整链路";

    @Autowired
    private GroupManageService groupManageService;
    @Autowired
    private GroupMemberService groupMemberService;
    @Autowired
    private AdvertisementService advertisementService;
    @Autowired
    private ChooseOrderService chooseOrderService;
    @Autowired
    private ForwardQuaterService forwardQuaterService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ChooseOrderDao chooseOrderDao;
    @Autowired
    private ExperienceDao experienceDao;
    @Autowired
    private GroupManageDao groupManageDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldCompleteBusinessChainForThreeStudentsAndGenerateReports() throws Exception {
        MockHttpSession stu001 = session("stu001", 3);
        MockHttpSession stu002 = session("stu002", 3);
        MockHttpSession stu003 = session("stu003", 3);

        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setGroupName(GROUP_NAME);
        groupCreateDto.setTeacherId("teacher001");
        groupCreateDto.setMaxEnterpriseNumber(3);
        assertSuccess(groupManageService.createGroup(groupCreateDto, stu001));
        assertSuccess(groupMemberService.joinGroup("stu002", GROUP_NAME, stu002));
        assertSuccess(groupMemberService.joinGroup("stu003", GROUP_NAME, stu003));
        assertSuccess(groupManageService.startGame(GROUP_NAME, stu001));

        Map<String, String> userUniques = userUniques();
        assertEquals(3, userUniques.size());
        assertEquals(Integer.valueOf(3), queryInt("select count(*) from gamegroupmember where GROUPNAME=? and STATUS=1 and CURRENTPERIOD=1", GROUP_NAME));
        assertEquals(Integer.valueOf(3), queryInt("select count(*) from cashsheet where USERUNIQUE in (?, ?, ?)", userUniques.get("stu001"), userUniques.get("stu002"), userUniques.get("stu003")));
        assertEquals(Integer.valueOf(12), queryInt("select count(*) from adofuser where PERIOD=1"));
        assertEquals(Integer.valueOf(3), queryInt("select count(*) from adstatusofuser where PERIOD=1"));

        putFirstAdvertisement(userUniques.get("stu001"), 5000D);
        putFirstAdvertisement(userUniques.get("stu002"), 3000D);
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu001")));
        assertEquals(Integer.valueOf(0), queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME));
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu002")));
        assertEquals(Integer.valueOf(0), queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME));
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu003")));

        assertEquals(Integer.valueOf(3), queryInt("select count(*) from adstatusofuser where PERIOD=1 and FINISHADFLAG=1 and CHOOSEORDERFLAG=1"));
        int generatedChooseOrders = queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME);
        int marketProductCount = queryInt("select count(*) from marketbasic") * queryInt("select count(*) from productbasic");
        int activeMembers = queryInt("select count(*) from gamegroupmember where GROUPNAME=? and STATUS=1", GROUP_NAME);
        assertEquals(activeMembers * marketProductCount, generatedChooseOrders);
        assertEquals(Integer.valueOf(marketProductCount), queryInt("select count(*) from chooseorder where USERUNIQUE=? and PERIOD=1", userUniques.get("stu003")));
        assertFalse(advertisementService.checkGenerateOrderIfNecessary(userUniques.get("stu001")));
        assertEquals(generatedChooseOrders, queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME).intValue());
        Integer deletedChooseId = jdbcTemplate.queryForObject("select CHOOSEID from chooseorder where USERUNIQUE=? and PERIOD=1 order by CHOOSEID limit 1", Integer.class, userUniques.get("stu003"));
        jdbcTemplate.update("delete from chooseorder where CHOOSEID=?", deletedChooseId);
        assertEquals(generatedChooseOrders - 1, queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME).intValue());
        assertTrue(advertisementService.checkGenerateOrderIfNecessary(userUniques.get("stu001")));
        assertEquals(generatedChooseOrders, queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME).intValue());

        List<ChooseOrderVO> visibleOrders = chooseOrderService.findChooseOrderByMarketNameAndProductName(userUniques.get("stu001"));
        assertFalse(visibleOrders.isEmpty());
        Integer orderForStu001 = findSelectableOrderId(userUniques.get("stu001"));
        assertNotNull(orderForStu001);
        assertEquals(1, chooseOrderService.updateChoosingOrder(userUniques.get("stu001"), String.valueOf(orderForStu001)));
        assertEquals(Integer.valueOf(0), queryInt("select count(*) from chooseorder where USERUNIQUE=? and PERIOD=1 and CHOOSEVALUE<4", userUniques.get("stu001")));
        assertEquals(5, chooseOrderService.updateChoosingOrder(userUniques.get("stu002"), String.valueOf(orderForStu001)));
        Integer orderForStu002 = findSelectableOrderId(userUniques.get("stu002"));
        assertNotNull(orderForStu002);
        assertEquals(1, chooseOrderService.updateChoosingOrder(userUniques.get("stu002"), String.valueOf(orderForStu002)));

        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu001")));
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu002")));
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu003")));
        assertSuccess(forwardQuaterService.forwardStatus(userUniques.get("stu001")));
        assertEquals(Integer.valueOf(2), queryInt("select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?", userUniques.get("stu001")));

        jdbcTemplate.update("update gamegroupmember set STATUS=0 where USERUNIQUE=?", userUniques.get("stu003"));
        assertSuccess(forwardQuaterService.forwardStatus(userUniques.get("stu002")));
        assertEquals(Integer.valueOf(2), queryInt("select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?", userUniques.get("stu002")));

        Result reportBeforeFinish = reportService.viewReport(userUniques.get("stu001"));
        assertEquals(Integer.valueOf(0), reportBeforeFinish.getStatus());
        assertTrue(reportBeforeFinish.getData() instanceof Map);
        assertFalse(((Map) reportBeforeFinish.getData()).containsKey("businesses"));

        jdbcTemplate.update("update gamegroupmember set STATUS=2, CURRENTPERIOD=5 where GROUPNAME=?", GROUP_NAME);
        jdbcTemplate.update("update gamegroup set CURRENTPERIOD=5 where GROUPNAME=?", GROUP_NAME);
        jdbcTemplate.update("update gamegroupmemberdetail set CONFIRM=1, CONTRIBUTION=1 where GROUPNAME=?", GROUP_NAME);
        for (String userUnique : userUniques.values()) {
            experienceDao.insertContext(userUnique, "<p>" + userUnique + " 完成实验报告</p>");
        }

        for (String userUnique : userUniques.values()) {
            Result report = reportService.viewReport(userUnique);
            assertEquals(Integer.valueOf(0), report.getStatus());
            assertTrue(report.getData() instanceof HashMap);
            Map data = (Map) report.getData();
            assertTrue(data.containsKey("group"));
            assertTrue(data.containsKey("businesses"));
            assertTrue(data.containsKey("context"));
            assertTrue(String.valueOf(data.get("context")).contains("完成实验报告"));
        }
    }

    private MockHttpSession session(String userId, int status) {
        MockHttpSession session = new MockHttpSession();
        UserPo userPo = new UserPo();
        userPo.setUserId(userId);
        userPo.setStatus(status);
        session.setAttribute("userId", userId);
        session.setAttribute("userInfo", userPo);
        return session;
    }

    private Map<String, String> userUniques() {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList("select USERID, USERUNIQUE from gamegroupmember where GROUPNAME=?", GROUP_NAME);
        Map<String, String> userUniques = new HashMap<>();
        for (Map<String, Object> row : rows) {
            userUniques.put(String.valueOf(row.get("USERID")), String.valueOf(row.get("USERUNIQUE")));
        }
        return userUniques;
    }

    private void putFirstAdvertisement(String userUnique, Double money) {
        List<Integer> adIds = jdbcTemplate.queryForList(
                "select ADID from adofuser where USERUNIQUE=? and PERIOD=1",
                Integer.class,
                userUnique);
        assertFalse(adIds.isEmpty());
        for (Integer adId : adIds) {
            assertSuccess(advertisementService.updateAdvertisementForProduct(adId, money));
        }
    }

    private Integer findSelectableOrderId(String userUnique) {
        List<Integer> orderIds = jdbcTemplate.queryForList(
                "select a.ORDERID from allordersofgroup a " +
                        "join chooseorder c on a.GROUPNAME=c.GROUPNAME and a.PERIOD=c.PERIOD and a.MARKETNAME=c.MARKETNAME and a.PRODUCTNAME=c.PRODUCTNAME " +
                        "where c.USERUNIQUE=? and a.STATUS=0 and c.CHOOSEVALUE=(" +
                        "select min(c2.CHOOSEVALUE) from chooseorder c2 where c2.GROUPNAME=c.GROUPNAME and c2.PERIOD=c.PERIOD and c2.MARKETNAME=c.MARKETNAME and c2.PRODUCTNAME=c.PRODUCTNAME) " +
                        "order by a.ORDERID limit 1",
                Integer.class,
                userUnique);
        return orderIds.isEmpty() ? null : orderIds.get(0);
    }

    private Integer queryInt(String sql, Object... args) {
        return jdbcTemplate.queryForObject(sql, args, Integer.class);
    }

    private void assertSuccess(Result result) {
        assertEquals(result == null ? "null result" : result.getMessage(), Integer.valueOf(0), result.getStatus());
    }

    @Test
    public void shouldAdvanceThroughAllPeriodsAndHandleYearBoundary() throws Exception {
        MockHttpSession stu001 = session("stu001", 3);
        MockHttpSession stu002 = session("stu002", 3);
        MockHttpSession stu003 = session("stu003", 3);

        GroupCreateDto groupCreateDto = new GroupCreateDto();
        groupCreateDto.setGroupName(GROUP_NAME);
        groupCreateDto.setTeacherId("teacher001");
        groupCreateDto.setMaxEnterpriseNumber(3);
        assertSuccess(groupManageService.createGroup(groupCreateDto, stu001));
        assertSuccess(groupMemberService.joinGroup("stu002", GROUP_NAME, stu002));
        assertSuccess(groupMemberService.joinGroup("stu003", GROUP_NAME, stu003));
        assertSuccess(groupManageService.startGame(GROUP_NAME, stu001));

        Map<String, String> userUniques = userUniques();
        assertEquals(3, userUniques.size());

        // ====== Period 1: advertise, confirm, end selection ======
        putFirstAdvertisement(userUniques.get("stu001"), 5000D);
        putFirstAdvertisement(userUniques.get("stu002"), 3000D);
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu001")));
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu002")));
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu003")));

        int marketProductCount = queryInt("select count(*) from marketbasic") * queryInt("select count(*) from productbasic");
        int activeMembers = queryInt("select count(*) from gamegroupmember where GROUPNAME=? and STATUS=1", GROUP_NAME);
        assertEquals(Integer.valueOf(activeMembers * marketProductCount),
                queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=1", GROUP_NAME));
        assertEquals(Integer.valueOf(3),
                queryInt("select count(*) from adstatusofuser where PERIOD=1 and FINISHADFLAG=1 and CHOOSEORDERFLAG=1"));

        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu001")));
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu002")));
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu003")));
        assertEquals(Integer.valueOf(1),
                queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=1", userUniques.get("stu001")));
        assertEquals(Integer.valueOf(1),
                queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=1", userUniques.get("stu002")));
        assertEquals(Integer.valueOf(1),
                queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=1", userUniques.get("stu003")));

        // ====== Forward ALL three through periods 1→2→3→4 ======
        for (int srcPeriod = 1; srcPeriod <= 3; srcPeriod++) {
            for (String key : new String[]{"stu001", "stu002", "stu003"}) {
                Result forwardResult = forwardQuaterService.forwardStatus(userUniques.get(key));
                assertSuccess(forwardResult);
                ForwardQuaterVo vo = (ForwardQuaterVo) forwardResult.getData();
                assertEquals("Forward from " + srcPeriod + "→" + (srcPeriod + 1) + " failed for " + key,
                        Integer.valueOf(ForwardStatusEnum.RECOMMEND_A_CYCLE.getStatus()), vo.getForwardStatus());
            }
            int expectedPeriod = srcPeriod + 1;
            for (String key : new String[]{"stu001", "stu002", "stu003"}) {
                assertEquals(key + " should be at period " + expectedPeriod,
                        Integer.valueOf(expectedPeriod),
                        queryInt("select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?", userUniques.get(key)));
            }
        }

        assertEquals(Integer.valueOf(4),
                queryInt("select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?", userUniques.get("stu001")));

        // ====== Forward period 4→5 (new year begin) ======
        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            Result forwardResult = forwardQuaterService.forwardStatus(userUniques.get(key));
            assertSuccess(forwardResult);
            ForwardQuaterVo vo = (ForwardQuaterVo) forwardResult.getData();
            assertEquals("Forward 4→5 failed for " + key,
                    Integer.valueOf(ForwardStatusEnum.RECOMMEND_A_CYCLE.getStatus()), vo.getForwardStatus());
        }
        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            assertEquals(key + " should be at period 5",
                    Integer.valueOf(5),
                    queryInt("select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?", userUniques.get(key)));
        }

        // ====== Verify period 5 init: adofuser & adstatusofuser rows ======
        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            String uq = userUniques.get(key);
            assertEquals("adofuser count for period 5: " + key,
                    Integer.valueOf(marketProductCount),
                    queryInt("select count(*) from adofuser where USERUNIQUE=? and PERIOD=5", uq));
            assertEquals("adstatusofuser count for period 5: " + key,
                    Integer.valueOf(1),
                    queryInt("select count(*) from adstatusofuser where USERUNIQUE=? and PERIOD=5", uq));
            assertEquals("FINISHADFLAG should be 0 at period 5 begin: " + key,
                    Integer.valueOf(0),
                    queryInt("select FINISHADFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=5", uq));
            assertEquals("FINISHORDERFLAG should be 0 at period 5 begin: " + key,
                    Integer.valueOf(0),
                    queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=5", uq));
            assertEquals("CHOOSEORDERFLAG should be 0 at period 5 begin: " + key,
                    Integer.valueOf(0),
                    queryInt("select CHOOSEORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=5", uq));
        }

        // ====== Verify forward returns status 2 at year-begin (no ads yet) ======
        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            Result forwardResult = forwardQuaterService.forwardStatus(userUniques.get(key));
            assertSuccess(forwardResult);
            ForwardQuaterVo vo = (ForwardQuaterVo) forwardResult.getData();
            assertEquals("Year-begin should return status 2 (need ads) for " + key,
                    Integer.valueOf(ForwardStatusEnum.ADVERTISEMENT_AND_ORDER_NOT_YET.getStatus()), vo.getForwardStatus());
        }

        // ====== Period 5: advertise and confirm ======
        putAdvertisementForPeriod(userUniques.get("stu001"), 5, 5000D);
        putAdvertisementForPeriod(userUniques.get("stu002"), 5, 3000D);
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu001")));
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu002")));
        assertSuccess(advertisementService.updateAdvertisementFinish(userUniques.get("stu003")));

        assertEquals("All three should have CHOOSEORDERFLAG=1 after ads",
                Integer.valueOf(3),
                queryInt("select count(*) from adstatusofuser where PERIOD=5 and FINISHADFLAG=1 and CHOOSEORDERFLAG=1"));
        assertEquals("Period 5 chooseorder should cover all members x market x product",
                Integer.valueOf(activeMembers * marketProductCount),
                queryInt("select count(*) from chooseorder where GROUPNAME=? and PERIOD=5", GROUP_NAME));
        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            assertEquals(key + " should have chooseorder for period 5",
                    Integer.valueOf(marketProductCount),
                    queryInt("select count(*) from chooseorder where USERUNIQUE=? and PERIOD=5", userUniques.get(key)));
        }

        // ====== Period 5: end selection and forward 5→6 ======
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu001")));
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu002")));
        assertEquals(1, chooseOrderService.updateEndChooseOrder(userUniques.get("stu003")));
        assertEquals(Integer.valueOf(1),
                queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=5", userUniques.get("stu001")));
        assertEquals(Integer.valueOf(1),
                queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=5", userUniques.get("stu002")));
        assertEquals(Integer.valueOf(1),
                queryInt("select FINISHORDERFLAG from adstatusofuser where USERUNIQUE=? and PERIOD=5", userUniques.get("stu003")));

        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            Result forwardResult = forwardQuaterService.forwardStatus(userUniques.get(key));
            assertSuccess(forwardResult);
            ForwardQuaterVo vo = (ForwardQuaterVo) forwardResult.getData();
            assertEquals("Forward 5→6 failed for " + key,
                    Integer.valueOf(ForwardStatusEnum.RECOMMEND_A_CYCLE.getStatus()), vo.getForwardStatus());
        }
        for (String key : new String[]{"stu001", "stu002", "stu003"}) {
            assertEquals(key + " should be at period 6",
                    Integer.valueOf(6),
                    queryInt("select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?", userUniques.get(key)));
        }
    }

    private void putAdvertisementForPeriod(String userUnique, int period, Double money) {
        List<Integer> adIds = jdbcTemplate.queryForList(
                "select ADID from adofuser where USERUNIQUE=? and PERIOD=?",
                Integer.class,
                userUnique, period);
        assertFalse("No ad rows for period " + period, adIds.isEmpty());
        for (Integer adId : adIds) {
            assertSuccess(advertisementService.updateAdvertisementForProduct(adId, money));
        }
    }
}
