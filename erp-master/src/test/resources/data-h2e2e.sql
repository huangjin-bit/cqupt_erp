INSERT INTO adminuser(ADMINID, ADMINNAME, POSITION, EMAIL, TEL, REM1, ROLE) VALUES
('teacher001', '测试老师', 'teacher', 'teacher@example.com', '10000', '', 'teacher');

INSERT INTO approveduser(USERID, USERNAME, PASSWORD, MAJOR, CLASS, SID, EMAIL, TEL, GENDER) VALUES
('stu001', '张启悦', '123456', '软件工程', '1班', '2026001', 'stu001@example.com', '10001', '男'),
('stu002', '满珈汝', '123456', '软件工程', '1班', '2026002', 'stu002@example.com', '10002', '女'),
('stu003', '边界同学', '123456', '软件工程', '1班', '2026003', 'stu003@example.com', '10003', '男');

INSERT INTO marketbasic(MARKETNAME) VALUES ('本地市场'), ('区域市场');
INSERT INTO productbasic(PRODUCTNAME) VALUES ('P1'), ('P2');

INSERT INTO isobasic(ISONAME, STATUS, RESEARCHPERIOD, RESEARCHCOST, MAINTAINCOST) VALUES
('ISO9000', 1, 1, 1000, 100),
('ISO14000', 1, 1, 1500, 100);

INSERT INTO orderpredictionbasic(PRODUCTNAME, PRICE, MOUNT, PRICEDIFFERENCE, MOUNTDIFFERENCE, PRICEFLOAT, MOUNTFLOAT) VALUES
('P1', 20, 20, 2, 3, 0.05, 0.05),
('P2', 30, 16, 3, 3, 0.05, 0.05);

INSERT INTO accountonebasic(ONEITEM, TYPE1) VALUES
('现金', '资产'),
('股东资本', '权益'),
('利润留存', '权益'),
('净利润', '权益');

INSERT INTO balancesheettextbasic(CONTENTVALUE, ROWNUMBER, COLNUMBER) VALUES
('资产', 1, 1), ('期初', 1, 2), ('期末', 1, 3),
('现金', 2, 1), ('权益', 8, 1), ('股东资本', 9, 1), ('利润留存', 10, 1), ('净利润', 11, 1);

INSERT INTO balancesheetoneitembasic(ONEITEM, ROWNUMBER_FIRST, COLNUMBER_FIRST, ROWNUMBER_LAST, COLNUMBER_LAST) VALUES
('现金', 2, 2, 2, 3),
('股东资本', 9, 2, 9, 3),
('利润留存', 10, 2, 10, 3),
('净利润', 11, 2, 11, 3);

INSERT INTO balancesheetcalbasic(CALCELLID, CALROW_FIRST, CALCOL_FIRST, CALROW_LAST, CALCOL_LAST) VALUES
(1, 14, 2, 14, 3);

INSERT INTO balancesheetcalrelationbasic(CALCELLID, ONEITEM, SIGNAL) VALUES
(1, '现金', 1),
(1, '股东资本', 1),
(1, '利润留存', 1),
(1, '净利润', 1);
