package com.cqupt.mislab.erpn.commons.utils.report;

import com.cqupt.mislab.erpn.commons.constant.GradeEnum;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.enterpriseevaluate.EnterPriseEvaluateDao;
import com.cqupt.mislab.erpn.dao.finance.balance.BalanceSheetDao;
import com.cqupt.mislab.erpn.dao.group.GameGroupMemberDetailDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.report.ExperienceDao;
import com.cqupt.mislab.erpn.model.dto.gamegroup.GameGroupMemberDTO;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberDetail;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author 张烈文
 * @date 2021/12/3 19:41
 */

@Component("rayConcreteAlgorithm")
public class RayConcreteAlgorithm implements ReportAlgorithmStrategy{

    @Autowired
    private GameGroupMemberDetailDao gameGroupMemberDetailDao;

    @Autowired
    private EnterPriseEvaluateDao enterPriseEvaluateDao;


    /**
     * 算法：
     * 1.先根据企业的运营期数排名
     * 2.若存在企业经营期数相同的情况就需要比较status：2(完成比赛)>-1（重新开始）>0（破产）
     * 3.若存在status相同的企业：则再去比较企业的所有者权益
     * 4.得到最终排名然后按照等差数列产生分数
     * @param memberByGameGroup
     * @param maxScore
     * @param minScore
     * @return
     */
    @Override
    public List<GameGroupMemberDetail> produceScore(List<GameGroupMemberInfo> memberByGameGroup, Float maxScore, Float minScore) {
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
}
