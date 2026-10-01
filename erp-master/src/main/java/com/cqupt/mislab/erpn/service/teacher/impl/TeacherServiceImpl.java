package com.cqupt.mislab.erpn.service.teacher.impl;

import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.dao.teacher.TeacherDao;
import com.cqupt.mislab.erpn.dao.user.LoginUserDao;
import com.cqupt.mislab.erpn.dao.user.RegisterUserDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.teacher.ImportResultRow;
import com.cqupt.mislab.erpn.model.dto.teacher.GameImportRow;
import com.cqupt.mislab.erpn.model.dto.teacher.StudentImportRow;
import com.cqupt.mislab.erpn.model.dto.teacher.SystemResetSummary;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.entity.user.BasicUser;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import com.cqupt.mislab.erpn.model.po.TeacherPo;
import com.cqupt.mislab.erpn.service.teacher.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * @program: com.cqupt.mislab.erpn.service
 * @description:
 * @author: keji0
 * @date: 2020/7/28 0:58
 */

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherDao teacherDao;
    @Autowired
    private LoginUserDao loginUserDao;
    @Autowired
    private RegisterUserDao registerUserDao;

    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

/*    @Override
    public Boolean importStudents(List<List<String>> infos,String adminId) {
        try {
            for (List<String> info:infos) {
                teacherDao.importApprovedUser(info);
                teacherDao.insertReferences(info.get(0),adminId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            log.info("插入记录失败");
            return false;
        }
        return true;
    }*/    @Override
    public List<ImportResultRow> importStudentsDetailed(List<StudentImportRow> rows, String adminId) {
        List<ImportResultRow> results = new ArrayList<>();
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        HashSet<String> seenUserIds = new HashSet<>();
        HashSet<String> seenStudentIds = new HashSet<>();

        for (StudentImportRow row : rows) {
            ApprovedUser user = row.getUser();
            if (row.getError() != null) {
                results.add(ImportResultRow.failure(row.getRowNumber(), null, null, null, row.getError()));
                continue;
            }
            String userId = user.getUserId();
            String studentId = user.getSId();
            if (!seenUserIds.add(userId)) {
                results.add(ImportResultRow.failure(row.getRowNumber(), user.getUserName(), studentId, userId, "CSV 中用户 ID 重复"));
                continue;
            }
            if (!seenStudentIds.add(studentId)) {
                results.add(ImportResultRow.failure(row.getRowNumber(), user.getUserName(), studentId, userId, "CSV 中学号重复"));
                continue;
            }
            try {
                transactionTemplate.execute(status -> {
                    if (!Boolean.TRUE.equals(teacherDao.importApprovedUser(Collections.singletonList(user)))) {
                        throw new IllegalStateException("数据库插入用户失败");
                    }
                    teacherDao.batchInsertTeacherReferences(Collections.singletonList(user), adminId);
                    return null;
                });
                results.add(ImportResultRow.success(row.getRowNumber(), user.getUserName(), studentId, userId));
            } catch (Exception e) {
                log.error("批量导入第 {} 行失败，userId={}", row.getRowNumber(), userId, e);
                results.add(ImportResultRow.failure(row.getRowNumber(), user.getUserName(), studentId, userId, readableImportError(e)));
            }
        }
        return results;
    }

    private String readableImportError(Exception exception) {
        Throwable current = exception;
        while (current.getCause() != null) current = current.getCause();
        String message = current.getMessage();
        if (message == null || message.trim().isEmpty()) return "数据库写入失败";
        if (message.toLowerCase().contains("duplicate")) return "用户 ID 或学号已存在";
        return message;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean importStudents(List<ApprovedUser> infos,String adminId) {
        try {
            if (!Boolean.TRUE.equals(teacherDao.importApprovedUser(infos))) {
                throw new IllegalStateException("插入学生失败");
            }
            teacherDao.batchInsertTeacherReferences(infos,adminId);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error("插入记录失败", e);
            return false;
        }
        return true;
    }

    @Override
    public Boolean deleteBatchUsers(String[] ids) throws Exception {
        for (String id:ids) {
            if (!deleteApprovedUser(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean deleteApprovedUser(String userId) throws Exception {
        try {
            if(loginUserDao.deleteApprovedUser(userId)&&teacherDao.deleteReference(userId))
                return true;
        }catch (Exception  e){
            log.info("删除失败！");
            throw new Exception("操作失败");
        }
        return false;
    }

    @Override
    public List<BasicUser> rangeSearch(String keyword, int status) {
        List<BasicUser> users = teacherDao.findUsersUnclear(status, keyword);
        return users;
    }

    // TODO
    @Override
    public BasicUser exactSearch(String userId,int status) {
        BasicUser basicUser = teacherDao.findBasicUser(status, userId);
        if(basicUser != null) return basicUser;
        return null;
    }

    @Override
    public boolean resetPassword(int status, String userId) {
        BasicUser basicUser = exactSearch(userId, status);
        if(basicUser != null){
            if(status == 0)teacherDao.resetPasswordApproved(userId);
            if(status == 1)teacherDao.resetPasswordRegister(userId);
            return true;
        }
        return false;
    }

    @Override
    public Boolean teacherLogin(String teacherId, String password, HttpSession session) {
        TeacherPo teacherPo = teacherDao.judgeAccount(teacherId, password);
        if(teacherPo !=null){
            session.setAttribute("teacherPo",teacherPo);
            return true;
        }
        return false;
    }

    @Override
    public List pageUser(int page,int limit,int status) {
        Integer maxPage = countRecordsApprovedUser(limit);

        if(maxPage <= 0||page>maxPage||page<0){
            log.info("教师端查询页数超越界限！");
            return null;
        }
        int start = (page - 1)*limit;
        if(status == 0) {
            return teacherDao.pageFindApprovedUser(start, limit);
        }
        if(status == 1) {
            return   teacherDao.pageFindRegisterUser(start, limit);
        }
        return null;
    }

    @Override
    public List getAllUser(int status) {
        if(status == 0){
            return teacherDao.findAllApprovedUser();
        }
        else{
            return  teacherDao.findAllRegister();
        }
    }

//    @Override
//    public List<RegisterUser> pageRegisterUser(int page,int limit) {
//        int maxPage = countRecordsRegisterUser(limit);
//
//        if(page>maxPage||page<0){
//            log.info("教师端查询页数超越界限！");
//            return null;
//        }
//        int start = (page - 1)*limit;
//        List<RegisterUser> registerUsers = teacherDao.pageFindRegisterUser(start,limit);
//        return registerUsers;
//    }

    @Override
    public Integer countRecordsApprovedUser(int limit) {
        int records = teacherDao.countRecordsApprovedUser();
//        if(limit > records)return null;
        return records/limit+1;
    }

    @Override
    public Integer countRecordsRegisterUser(int limit) {
        int records = teacherDao.countRecordsApprovedUser();
        if(limit > records)return null;
        return records/limit+1;
    }

    @Override
    public Result findAllRegister() {
        return ResultUtil.success(teacherDao.findAllRegister());
    }

    @Override
    public Result findAllApprovedUser() {
        return ResultUtil.success(teacherDao.findAllApprovedUser());
    }

    @Override
    public Result passRegisterUser(String userId, String adminId) {
        RegisterUser registerUser = registerUserDao.findRegister(userId);
        loginUserDao.addApprovedUser(registerUser);
        teacherDao.insertReferences(userId,adminId);
        registerUserDao.deleteRegisterByUserId(userId);
        return ResultUtil.success();
    }

    @Override
    public Result refuseRegisterUser(String userId, String adminId) {
        registerUserDao.deleteRegisterByUserId(userId);
        return ResultUtil.success();
    }


    @Override
    public Result passBatchRegisterUsers(String[] userIds, String adminId) {
        for (int i = 0; i < userIds.length; i++) {
            passRegisterUser(userIds[i],adminId);
        }
        return ResultUtil.success();
    }

    @Override
    public Result refuseBatchRegisterUsers(String[] userIds, String adminId) {
        for (int i = 0; i < userIds.length; i++) {
            refuseRegisterUser(userIds[i],adminId);
        }
        return ResultUtil.success();
    }    @Override
    public Result importGameStudentsDetailed(List<GameImportRow> rows, String adminId) {
        HashSet<String> allowedUsers = teacherDao.findAllApprovedUserByAdminId(adminId);
        HashSet<String> seenUsers = new HashSet<>();
        List<ImportResultRow> results = new ArrayList<>();
        List<String> validUserIds = new ArrayList<>();

        for (GameImportRow row : rows) {
            if (row.getError() != null) {
                results.add(ImportResultRow.failure(row.getRowNumber(), "", row.getUserId(), row.getUserId(), row.getError()));
                continue;
            }
            if (!seenUsers.add(row.getUserId())) {
                results.add(ImportResultRow.failure(row.getRowNumber(), "", row.getUserId(), row.getUserId(), "CSV 中用户 ID 重复"));
                continue;
            }
            if (!allowedUsers.contains(row.getUserId())) {
                results.add(ImportResultRow.failure(row.getRowNumber(), "", row.getUserId(), row.getUserId(), "用户未被当前教师导入或不存在"));
                continue;
            }
            validUserIds.add(row.getUserId());
            results.add(ImportResultRow.success(row.getRowNumber(), "", row.getUserId(), row.getUserId()));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("results", results);
        data.put("users", teacherDao.findApprovedUsersByGameUsers(validUserIds));
        return ResultUtil.success(data);
    }


    @Override
    public Result importGameStudents(List<String> gameUsers, String adminId) {
        HashSet<String> allApprovedUser = teacherDao.findAllApprovedUserByAdminId(adminId);
        for(String gameUser:gameUsers){
            if(!allApprovedUser.contains(gameUser)){
                return ResultUtil.error("请确认需要开始比赛的人员已经注册在系统中");
            }
        }
        return ResultUtil.success(teacherDao.findApprovedUsersByGameUsers(gameUsers));
    }
    private static final List<String> GAME_DATA_TABLES = Arrays.asList(
            "adstatusofuser",
            "adofuser",
            "chooseorder",
            "ordersofuser",
            "allordersofgroup",
            "predictionofgroup",
            "loanofuser",
            "willreceive",
            "willpay",
            "cashsheet",
            "balancesheet",
            "accounthead",
            "accountdetail",
            "profitsheet",
            "taxsheet",
            "factory_making",
            "factory_renting",
            "factory_using",
            "productline",
            "productofuser",
            "materialofuser",
            "buyingmaterial",
            "developedmarket",
            "developingmarket",
            "developedproduct",
            "developingproduct",
            "developediso",
            "developingiso",
            "undevelopiso",
            "undevelopmarket",
            "undevelopproduct",
            "experience",
            "gamegroupmemberdetail",
            "gamegroupmember",
            "gamegroup",
            "stu_teacher_reference",
            "registeruser",
            "approveduser"
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SystemResetSummary resetSystemData() {
        SystemResetSummary summary = SystemResetSummary.empty();
        for (String tableName : GAME_DATA_TABLES) {
            int before = teacherDao.countTableRows(tableName);
            if (before > 0) {
                int deleted = teacherDao.deleteAllTableRows(tableName);
                summary.addTable(tableName, deleted > 0 ? deleted : before);
            } else {
                summary.addTable(tableName, 0);
            }
        }
        return summary;
    }
}

