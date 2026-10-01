package com.cqupt.mislab.erpn.service.report.Impl;

import com.cqupt.mislab.erpn.commons.constant.DownLoadInfoEnum;
import com.cqupt.mislab.erpn.commons.constant.GradeEnum;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.utils.report.RayConcreteAlgorithm;
import com.cqupt.mislab.erpn.commons.utils.report.ReportContext;
import com.cqupt.mislab.erpn.commons.utils.report.WordUtil;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.enterpriseevaluate.EnterPriseEvaluateDao;
import com.cqupt.mislab.erpn.dao.finance.balance.BalanceSheetDao;
import com.cqupt.mislab.erpn.dao.group.GameGroupMemberDetailDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.report.ExperienceDao;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;
import com.cqupt.mislab.erpn.model.dto.report.BusinessReport;
import com.cqupt.mislab.erpn.model.dto.report.Group;
import com.cqupt.mislab.erpn.model.dto.report.Row;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.entity.report.Experience;
import com.cqupt.mislab.erpn.model.vo.finance.BalanceVO;
import com.cqupt.mislab.erpn.service.report.ReportService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/10/7 12:22
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {


    /**
     *
     */
    @Autowired
    private ReportContext reportContext;

    @Autowired
    private GameGroupMemberDetailDao gameGroupMemberDetailDao;
    @Autowired
    private GroupManageDao groupManageDao;
    @Autowired
    private EnterPriseEvaluateDao enterPriseEvaluateDao;
    @Autowired
    private ExperienceDao experienceDao;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private BalanceSheetDao balanceSheetDao;
    @Autowired
    private WordUtil wordUtil;


    @Override
    @Transactional
    public synchronized Result downLoadReport(String userUnique) {
        try {
            HashMap<String, Object> map = getDataMap(userUnique);
            if(map.containsKey("data")) {
                HashMap<String, Object> ans = new HashMap<>();
                List<GameGroupMemberDetail> finalMemberDetail = (List<GameGroupMemberDetail>) map.get("data");
//           4.写入数据库
                updateMemberDetailInfo(finalMemberDetail);
//            5.生成报告
                HashMap<String, Object> dataMap = generateDataToReport(userUnique);
                String wordPath = wordUtil.generateWord(dataMap, userUnique);
                ans.put("path", wordPath);
                return ResultUtil.success(ans);
            }
            return ResultUtil.error(map);

        } catch (Exception e) {
            log.error("message:{}",e.getMessage());
            return ResultUtil.error();
        }
    }

    /**
     * 向数据库写入企业人人员信息
     * @param finalMemberDetail
     */
    private void updateMemberDetailInfo(List<GameGroupMemberDetail> finalMemberDetail) {
            for (GameGroupMemberDetail groupMemberDetail : finalMemberDetail) {
                String studentNo = groupMemberDetail.getStudentNo();
                String curUserUnique = groupMemberDetail.getUserUnique();
                Float score = groupMemberDetail.getScore();
                String grade = groupMemberDetail.getGrade();
                gameGroupMemberDetailDao.updateMemberDetailByGroupMemberDetail(curUserUnique, studentNo, score, grade);
            }
    }

    @Override
    @Transactional
    public synchronized Result TeacherDownLoadReport(String groupName) throws Exception {
        List<String> member = commonDao.findMemberUseruniqueByGroupName(groupName);
        List<HashMap<String, Object>> all = new ArrayList<>();
        for(int i = 0; i < member.size(); i++) {
            String userUnique = member.get(i);
            try {
                HashMap<String, Object> map = getDataMap(userUnique);
                if(map.containsKey("data")) {
                    HashMap<String, Object> ans = new HashMap<>();
                    List<GameGroupMemberDetail> finalMemberDetail = (List<GameGroupMemberDetail>) map.get("data");
                    //4.写入数据库
                    updateMemberDetailInfo(finalMemberDetail);
                    HashMap<String, Object> dataMap = generateDataToReport(userUnique);
                    all.add(dataMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        HashMap<String, Object> objectObjectHashMap = new HashMap<>();
        objectObjectHashMap.put("students", all);
        String wordPath = wordUtil.generateWordTeacher(objectObjectHashMap, groupName);
        objectObjectHashMap.put("path", wordPath);
        return ResultUtil.success(objectObjectHashMap);
    }


    private HashMap<String, Object> getDataMap(String userUnique) {
        HashMap<String, Object> ans = new HashMap<>();
        GameGroupInfo gameGroupInfo = groupManageDao.findGameGroupInfoByUserUnique(userUnique);
        String gameGroup = gameGroupInfo.getGroupName();
        List<GameGroupMemberInfo> groupMemberInfos = groupManageDao.findGroupMemberByGameGroup(gameGroup);
        for (GameGroupMemberInfo groupMemberInfo : groupMemberInfos) {
            if (groupMemberInfo.getStatus() == 1 || groupMemberInfo.getStatus() == 10) {
                ans.put("code", DownLoadInfoEnum.MEMBER_STILL_PLAY_GAME.getCode());
                ans.put("message", DownLoadInfoEnum.MEMBER_STILL_PLAY_GAME.getMessage());
                return ans;
            }
        }
//        2.判断教师是否已经确认了所有人的贡献度
        log.info("判断教师是否已经确认了所有人的贡献度");
        if (!isTeacherConfirm(groupMemberInfos)) {
            ans.put("code", DownLoadInfoEnum.TEACHER_NOT_CONFIRM.getCode());
            ans.put("message", DownLoadInfoEnum.TEACHER_NOT_CONFIRM.getMessage());
            return ans;
        }
//        3.开始打分出成绩,传入最高分，最低分
        float maxScore = gameGroupInfo.getMaxScore();
        float minScore = gameGroupInfo.getMinScore();
//        List<GameGroupMemberDetail> finalMemberDetail = produceScore(groupMemberInfos, maxScore, minScore);
        List<GameGroupMemberDetail> finalMemberDetail = reportContext.getScores(groupMemberInfos, maxScore, minScore);
        ans.put("data", finalMemberDetail);
        return ans;
    }

    @Override
    public Result viewReport(String userUnique) {
        HashMap<String, Object> res= getDataMap(userUnique);
        if (res.containsKey("data")) {
            List<GameGroupMemberDetail> finalMemberDetail = (List<GameGroupMemberDetail>) res.get("data");
            updateMemberDetailInfo(finalMemberDetail);
            HashMap<String, Object> dataMap = generateDataToReport(userUnique);
            return ResultUtil.success(dataMap);
        }
        return ResultUtil.success(res);
    }

    @Override
    public Result viewReportByTeacher(String groupName) {
        List<String> memberUseruniqueByGroupName = commonDao.findMemberUseruniqueByGroupName(groupName);
        List<HashMap> data = new ArrayList<>();
        for (String userUnique: memberUseruniqueByGroupName) {
            HashMap<String, Object> dataMap = getDataMap(userUnique);
            if (dataMap.containsKey("data")) {
//            1.写入数据库然后再查询返回数据
                List<GameGroupMemberDetail> finalMemberDetail = (List<GameGroupMemberDetail>) dataMap.get("data");
                updateMemberDetailInfo(finalMemberDetail);
                HashMap<String, Object> reportMap = generateDataToReport(userUnique);
                //return ResultUtil.success(dataMap);
                data.add(reportMap);
            }else {
                data.add(dataMap);
            }

        }
        return ResultUtil.success(data);
    }





    /**
     * 为报告生成数据
     * @param userUnique
     * @return
     */
    private HashMap<String, Object> generateDataToReport(String userUnique) {
        HashMap<String, Object> ans = new HashMap<>();

//        1.查询所有组员

        List<GameGroupMemberDetail> allInOneTeam = gameGroupMemberDetailDao.findAllInOneTeam(userUnique);
        String teamLeaderName = groupManageDao.findGroupMemberByUserUnique(userUnique);
        log.info("查询所有组员：{}，teamLeaderName:{}", allInOneTeam, teamLeaderName);
//        2.查询实验心得
        Experience experience = experienceDao.selectOneByUserUnique(userUnique);
        String context = "";
        if(experience!=null) {
            context = dealContext(experience.getContext());
        }
//        3.查出每年经营状况
        List<BusinessReport> businesses = new ArrayList<>();
        int periodsOfYear = commonDao.findPeriodsOfOneYearByUserUnique(userUnique);
        int currentPeriod = commonDao.findCurrentPeriod(userUnique);
        int year = (currentPeriod - 1) / 4 + 1;
        for (int i = 0; i < year; i++) {
            BusinessReport businessReport = new BusinessReport();
            int curYear = i + 1;
            int period = 4;
            if (curYear == year) {
                period = currentPeriod - i * periodsOfYear;
            }
            businessReport.setPeriod(period);
            businessReport.setYear(curYear);
            int checkPeriod = period + (curYear - 1) * periodsOfYear;

            List<Row> rows = decorateRows(userUnique, checkPeriod);
            businessReport.setRows(rows);
            businesses.add(businessReport);
        }
//        封装数据
        Group group = new Group();
        group.setTeamLeader(teamLeaderName);
        group.setMembers(allInOneTeam);
        ans.put("group", group);
        ans.put("businesses", businesses);
//        TODO 需要将填写的实验心得处理一下
        ans.put("context", context);
        log.info("封装数据：{}", ans);
        return ans;

    }

    private static String dealContext(String context) {
        if (context == null) {
            return "";
        }else{
            context = context.replace("<p>", "");
            context = context.replace("</p>", "<w:br/>");
            context = context.replace("<br>", "<w:br/>");
            context = context.replace("\n\n", "<w:br/>");
            return context;
        }
    }

    /**
     * 封装数据库返回的数据
     * @param userUnique
     * @param checkPeriod
     * @return
     */
    private List<Row> decorateRows(String userUnique,Integer checkPeriod) {
        List<BalanceVO> balanceSheet = balanceSheetDao.findBalanceSheet(userUnique, checkPeriod);
        List<Row> rows = new ArrayList<>();
        for (int i = 1; i < 15; i++) {
            List<BalanceVO> balanceRows = new ArrayList<>();
            for (BalanceVO balanceVO : balanceSheet) {
                if (balanceVO.getRows().equals(i + 1)) {
                    balanceRows.add(balanceVO);
                }
            }
            Row row = decorateRow(balanceRows);
            rows.add(row);
        }
        return rows;
    }

    /**
     * 封装单行数据
     * @param balanceRows
     * @return
     */
    private Row decorateRow(List<BalanceVO> balanceRows) {
        Row row = new Row();
        for (BalanceVO balanceRow : balanceRows) {
            String content = balanceRow.getContent()==null?"":balanceRow.getContent();
            switch (balanceRow.getCols()) {
                case 1:
                    row.setCol1(content);
                    break;
                case 2:
                    row.setCol2(content);
                    break;
                case 3:
                    row.setCol3(content);
                    break;
                case 4:
                    row.setCol4(content);
                    break;
                case 5:
                    row.setCol5(content);
                    break;
                case 6:
                    row.setCol6(content);
                    break;
            }
        }
        return row;
    }

    /**
     * 打分出成绩
     * @param maxScore
     * @param minScore
     * @return
     */
    private  List<GameGroupMemberDetail> produceScore(List<GameGroupMemberInfo> memberByGameGroup,Float maxScore,Float minScore) {
//        1.根据企业经营的period及status以及所有者权益得出企业排名
        List<GameGroupMemberDTO> gameGroupMemberDTOSorted = memberByGameGroup.stream().map((item) -> {
            GameGroupMemberDTO gameGroupMemberDTO = new GameGroupMemberDTO();
            BeanUtils.copyProperties(item, gameGroupMemberDTO);
            /*
             *若状态为0，代表破产了，为了方便我们排序比较即（status=2(完成比赛)>-1(重新开始)>0(破产)）
             * 因此需要改变status=0时的状态码，这么我们改为-2
             */
            if (gameGroupMemberDTO.getStatus().equals(0)) {
                gameGroupMemberDTO.setStatus(-2);
            }
            Double ownerBenifit = enterPriseEvaluateDao.CalOwnerBenifit(item.getUserUnique(), item.getCurrentPeriod());

            gameGroupMemberDTO.setOwnerBenifit(ownerBenifit == null ? 0 : ownerBenifit);
            return gameGroupMemberDTO;
        }).sorted(Comparator.comparing(GameGroupMemberDTO::getCurrentPeriod, Comparator.reverseOrder())
                .thenComparing(GameGroupMemberDTO::getStatus, Comparator.reverseOrder())
                .thenComparing(GameGroupMemberDTO::getOwnerBenifit, Comparator.reverseOrder()))
                .collect(Collectors.toList());
//        2.将获得的企业排名中企业中的成员依次加入到集合中，得到所有人的排序集合
        List<GameGroupMemberDetail> res = new ArrayList<>();
        for (GameGroupMemberDTO gameGroupMemberDTO : gameGroupMemberDTOSorted) {
            List<GameGroupMemberDetail> allInOneTeam = gameGroupMemberDetailDao.findAllInOneTeam(gameGroupMemberDTO.getUserUnique());
//            对成员按照贡献升序排名
            Collections.sort(allInOneTeam, (o1, o2) -> (int) (o1.getContribution() - o2.getContribution()));
            res.addAll(res.size(), allInOneTeam);
        }
//        3.所有的成员你数量
        int allPeople = res.size();
//        4.获取教师设置的最高分及最低分，按照等差数列打出成绩并根据成绩得出评级
        float differences = maxScore - minScore;
        float publicDif = differences / allPeople;
        AtomicInteger index = new AtomicInteger(0);
        List<GameGroupMemberDetail> finalMemberDetail = res.stream().map((item) -> {
            Float score = maxScore - publicDif * index.get();
            BigDecimal bigDecimal = new BigDecimal(score);
            float curScore = bigDecimal.setScale(1, BigDecimal.ROUND_UP).floatValue();
            String curGrade = calculateGrade(curScore);
            item.setScore(curScore);
            item.setGrade(curGrade);
            index.getAndIncrement();
            return item;
        }).collect(Collectors.toList());

        return finalMemberDetail;
    }

    /**
     * 根据分数获得评级
     * @param curScore
     * @return
     */
    private String calculateGrade(Float curScore) {
        if (curScore >= GradeEnum.EXCELLENT.getScore()) {
            return GradeEnum.EXCELLENT.getGrade();
        } else if (curScore >= GradeEnum.GOOD.getScore()) {
            return GradeEnum.GOOD.getGrade();
        } else if (curScore >= GradeEnum.MEAN.getScore()) {
            return GradeEnum.MEAN.getGrade();
        } else if (curScore >= GradeEnum.PASSED.getScore()) {
            return GradeEnum.PASSED.getGrade();
        }else{
            return GradeEnum.FAILED.getGrade();
        }
    }


    /**
     * 判断教师是否已经确认了贡献度
     * @param groupMemberInfos
     * @return
     */
    private boolean isTeacherConfirm(List<GameGroupMemberInfo> groupMemberInfos) {
        for (GameGroupMemberInfo groupMemberInfo : groupMemberInfos) {
//            1.通过userUnique查询该企业下的所有成员信息
            String userUnique = groupMemberInfo.getUserUnique();
            List<GameGroupMemberDetail> allInOneTeam = gameGroupMemberDetailDao.findAllInOneTeam(userUnique);
//            已经确认的所有成员
            List<GameGroupMemberDetail> confirmPeople = gameGroupMemberDetailDao.findConfirmAll(userUnique);
//            若没有全部确认成员的贡献排名，则返回false
            if (confirmPeople==null||allInOneTeam.size() != confirmPeople.size()) {
                return false;
            }
        }
        return true;
    }

}
