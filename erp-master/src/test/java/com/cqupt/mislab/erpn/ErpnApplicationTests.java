package com.cqupt.mislab.erpn;

import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.cqupt.mislab.erpn.dao")
public class ErpnApplicationTests {

    @Autowired
    private GroupManageDao groupManageDao;

    @Test
    void contextLoads() {

        Boolean v = groupManageDao.deleteGroupMembersByGroupName("TEST987");
        System.out.println(v);

    }




}
