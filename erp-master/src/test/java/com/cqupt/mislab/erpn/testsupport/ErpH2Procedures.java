package com.cqupt.mislab.erpn.testsupport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ErpH2Procedures {

    public static int inigame(Connection connection, String groupName) throws SQLException {
        try (PreparedStatement selectMembers = connection.prepareStatement(
                "select USERID from gamegroupmember where GROUPNAME=? order by USERID")) {
            selectMembers.setString(1, groupName);
            try (ResultSet members = selectMembers.executeQuery()) {
                int index = 1;
                while (members.next()) {
                    String userId = members.getString("USERID");
                    String userUnique = groupName + "_" + userId;
                    updateMember(connection, groupName, userId, userUnique);
                    seedPerUserRows(connection, userUnique);
                    index++;
                }
            }
        }
        try (PreparedStatement updateGroup = connection.prepareStatement(
                "update gamegroup set CURRENTPERIOD=1, MAXSCORE=100, MINSCORE=60 where GROUPNAME=?")) {
            updateGroup.setString(1, groupName);
            updateGroup.executeUpdate();
        }
        return 1;
    }

    public static int bizNextDeal(Connection connection, String userUnique) throws SQLException {
        int period = currentPeriod(connection, userUnique);
        ensureBalanceRows(connection, userUnique, period);
        ensureAccountRows(connection, userUnique, period);
        return 1;
    }

    public static int adminChangeToHistory(Connection connection, String groupName) {
        return 1;
    }

    public static String format(Double value, int scale) {
        if (value == null) {
            return null;
        }
        BigDecimal decimal = BigDecimal.valueOf(value);
        return decimal.setScale(scale, RoundingMode.HALF_UP).toPlainString();
    }

    private static void updateMember(Connection connection, String groupName, String userId, String userUnique) throws SQLException {
        try (PreparedStatement update = connection.prepareStatement(
                "update gamegroupmember set USERUNIQUE=?, CURRENTPERIOD=1, STATUS=1 where GROUPNAME=? and USERID=?")) {
            update.setString(1, userUnique);
            update.setString(2, groupName);
            update.setString(3, userId);
            update.executeUpdate();
        }
    }

    private static void seedPerUserRows(Connection connection, String userUnique) throws SQLException {
        insertIfAbsent(connection, "cashsheet", "USERUNIQUE", userUnique,
                "insert into cashsheet(USERUNIQUE, CASH) values(?, 200000)");
        seedAdvertisements(connection, userUnique, 1);
        seedAdStatus(connection, userUnique, 1);
        seedDevelopedRows(connection, userUnique);
        ensureBalanceRows(connection, userUnique, 1);
        ensureAccountRows(connection, userUnique, 1);
    }

    private static void seedAdvertisements(Connection connection, String userUnique, int period) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement(
                "select MARKETNAME, PRODUCTNAME from marketbasic, productbasic")) {
            try (ResultSet rows = query.executeQuery()) {
                while (rows.next()) {
                    try (PreparedStatement exists = connection.prepareStatement(
                            "select count(*) from adofuser where USERUNIQUE=? and PERIOD=? and MARKETNAME=? and PRODUCTNAME=?")) {
                        exists.setString(1, userUnique);
                        exists.setInt(2, period);
                        exists.setString(3, rows.getString("MARKETNAME"));
                        exists.setString(4, rows.getString("PRODUCTNAME"));
                        try (ResultSet count = exists.executeQuery()) {
                            count.next();
                            if (count.getInt(1) == 0) {
                                try (PreparedStatement insert = connection.prepareStatement(
                                        "insert into adofuser(PERIOD, MARKETNAME, PRODUCTNAME, USERUNIQUE, MONEY) values(?, ?, ?, ?, 0)")) {
                                    insert.setInt(1, period);
                                    insert.setString(2, rows.getString("MARKETNAME"));
                                    insert.setString(3, rows.getString("PRODUCTNAME"));
                                    insert.setString(4, userUnique);
                                    insert.executeUpdate();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void seedAdStatus(Connection connection, String userUnique, int period) throws SQLException {
        try (PreparedStatement exists = connection.prepareStatement(
                "select count(*) from adstatusofuser where USERUNIQUE=? and PERIOD=?")) {
            exists.setString(1, userUnique);
            exists.setInt(2, period);
            try (ResultSet count = exists.executeQuery()) {
                count.next();
                if (count.getInt(1) == 0) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "insert into adstatusofuser(USERUNIQUE, PERIOD, FINISHADFLAG, FINISHORDERFLAG, CHOOSEORDERFLAG) values(?, ?, 0, 0, 0)")) {
                        insert.setString(1, userUnique);
                        insert.setInt(2, period);
                        insert.executeUpdate();
                    }
                }
            }
        }
    }

    private static void seedDevelopedRows(Connection connection, String userUnique) throws SQLException {
        try (PreparedStatement queryMarket = connection.prepareStatement("select MARKETNAME from marketbasic")) {
            try (ResultSet rows = queryMarket.executeQuery()) {
                while (rows.next()) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "insert into developedmarket(USERUNIQUE, MARKETNAME, STATUS, LASTSTATUS, BEGINTIME, ENDTIME, MAINTAINCOST) " +
                                    "select ?, ?, 1, 1, 1, 1, 0 where not exists (select 1 from developedmarket where USERUNIQUE=? and MARKETNAME=?)")) {
                        insert.setString(1, userUnique);
                        insert.setString(2, rows.getString("MARKETNAME"));
                        insert.setString(3, userUnique);
                        insert.setString(4, rows.getString("MARKETNAME"));
                        insert.executeUpdate();
                    }
                }
            }
        }
        try (PreparedStatement queryIso = connection.prepareStatement("select ISONAME from isobasic")) {
            try (ResultSet rows = queryIso.executeQuery()) {
                while (rows.next()) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "insert into developediso(USERUNIQUE, ISONAME, STATUS, LASTSTATUS, BEGINTIME, ENDTIME, MAINTAINCOST) " +
                                    "select ?, ?, 1, 1, 1, 1, 0 where not exists (select 1 from developediso where USERUNIQUE=? and ISONAME=?)")) {
                        insert.setString(1, userUnique);
                        insert.setString(2, rows.getString("ISONAME"));
                        insert.setString(3, userUnique);
                        insert.setString(4, rows.getString("ISONAME"));
                        insert.executeUpdate();
                    }
                }
            }
        }
    }

    private static void ensureBalanceRows(Connection connection, String userUnique, int period) throws SQLException {
        seedBalanceItem(connection, userUnique, period, "现金", 200000, 200000);
        seedBalanceItem(connection, userUnique, period, "股东资本", 200000, 200000);
        seedBalanceItem(connection, userUnique, period, "利润留存", 0, 1000);
        seedBalanceItem(connection, userUnique, period, "净利润", 0, 1000);
    }

    private static void seedBalanceItem(Connection connection, String userUnique, int period, String item, double begin, double end) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "insert into balancesheet(USERUNIQUE, PERIOD, ONEITEM, BEGINVALUE, ENDVALUE) " +
                        "select ?, ?, ?, ?, ? where not exists (select 1 from balancesheet where USERUNIQUE=? and PERIOD=? and ONEITEM=?)")) {
            insert.setString(1, userUnique);
            insert.setInt(2, period);
            insert.setString(3, item);
            insert.setDouble(4, begin);
            insert.setDouble(5, end);
            insert.setString(6, userUnique);
            insert.setInt(7, period);
            insert.setString(8, item);
            insert.executeUpdate();
        }
    }

    private static void ensureAccountRows(Connection connection, String userUnique, int period) throws SQLException {
        try (PreparedStatement exists = connection.prepareStatement(
                "select count(*) from accounthead where USERUNIQUE=? and HAPPENTIME=?")) {
            exists.setString(1, userUnique);
            exists.setInt(2, period);
            try (ResultSet count = exists.executeQuery()) {
                count.next();
                if (count.getInt(1) == 0) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "insert into accounthead(USERUNIQUE, HAPPENTIME, ACCOUNTIDDESCRIPTION) values(?, ?, ?)")) {
                        insert.setString(1, userUnique);
                        insert.setInt(2, period);
                        insert.setString(3, "H2经营快照");
                        insert.executeUpdate();
                    }
                }
            }
        }
    }

    private static int currentPeriod(Connection connection, String userUnique) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "select CURRENTPERIOD from gamegroupmember where USERUNIQUE=?")) {
            statement.setString(1, userUnique);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 1;
    }

    private static void insertIfAbsent(Connection connection, String tableName, String columnName, String value, String insertSql) throws SQLException {
        try (PreparedStatement exists = connection.prepareStatement(
                "select count(*) from " + tableName + " where " + columnName + "=?")) {
            exists.setString(1, value);
            try (ResultSet count = exists.executeQuery()) {
                count.next();
                if (count.getInt(1) == 0) {
                    try (PreparedStatement insert = connection.prepareStatement(insertSql)) {
                        insert.setString(1, value);
                        insert.executeUpdate();
                    }
                }
            }
        }
    }
}
