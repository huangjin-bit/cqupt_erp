package com.cqupt.mislab.erpn.service.user.impl;

import com.cqupt.mislab.erpn.commons.constant.*;
import com.cqupt.mislab.erpn.commons.exception.MyException;
import com.cqupt.mislab.erpn.dao.common.CommonDao;
import com.cqupt.mislab.erpn.dao.factory.FactoryUsingDao;
import com.cqupt.mislab.erpn.dao.finance.cash.CashDao;
import com.cqupt.mislab.erpn.dao.finance.tax.TaxSheetDao;
import com.cqupt.mislab.erpn.dao.group.GroupManageDao;
import com.cqupt.mislab.erpn.dao.user.LoginUserDao;
import com.cqupt.mislab.erpn.dao.user.RegisterUserDao;
import com.cqupt.mislab.erpn.exception.ServerException;
import com.cqupt.mislab.erpn.model.Result;
import com.cqupt.mislab.erpn.model.dto.UserStudentInfoRegisterDto;
import com.cqupt.mislab.erpn.model.entity.factory.FactoryUsing;
import com.cqupt.mislab.erpn.model.entity.finance.tax.TaxSheet;
import com.cqupt.mislab.erpn.model.entity.user.ApprovedUser;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupInfo;
import com.cqupt.mislab.erpn.model.entity.gameGroup.GameGroupMemberInfo;
import com.cqupt.mislab.erpn.model.entity.user.RegisterUser;
import com.cqupt.mislab.erpn.model.po.UserPo;
import com.cqupt.mislab.erpn.model.vo.user.UserInfoBeginGame;
import com.cqupt.mislab.erpn.service.user.UserService;
import com.cqupt.mislab.erpn.commons.utils.ResultUtil;
import com.cqupt.mislab.erpn.commons.utils.ServiceUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @program: com.cqupt.mislab.erpn.service.impl
 * @description:
 * @author: keji0
 * @date: 2020/7/20 10:11
 */

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private RegisterUserDao registerUserDao;
    @Autowired
    private LoginUserDao loginUserDao;
    @Autowired
    private CommonDao commonDao;
    @Autowired
    private GroupManageDao groupManageDao;
    @Autowired
    private CashDao cashDao;
    @Autowired
    private FactoryUsingDao factoryUsingDao;
    @Autowired
    private TaxSheetDao taxSheetDao;



    /**
     * 添加注册用户
     *
     * @param studentInfoRegisterDto
     * @return
     */
    @Override
    public Result addRegisterUser(UserStudentInfoRegisterDto studentInfoRegisterDto) {
        String userId = studentInfoRegisterDto.getUserId();
        log.info(studentInfoRegisterDto.toString());
        try{
            if(userId!=null && !"".equals(userId)){
                try {
                    if (isExistRegister(userId, "") == -1 &&isExistApprovedUser(userId, "") == -1) {
                        ServiceUtil.insertSuccess(registerUserDao.addRegisterUser(studentInfoRegisterDto.getUserId(),studentInfoRegisterDto.getUserName(),studentInfoRegisterDto.getPassword(),studentInfoRegisterDto.getMajor(),studentInfoRegisterDto.getSClass(),studentInfoRegisterDto.getSId(),studentInfoRegisterDto.getEmail(),studentInfoRegisterDto.getTel(),studentInfoRegisterDto.getGender()));
                        RegisterUser registerUser = registerUserDao.findRegister(userId);
                        Map<String, RegisterUser> map = new HashMap<>();
                        map.put("userMessage",registerUser);
                        return ResultUtil.success(map);
                    } else {
                        throw new MyException(ResultEnum.HASEXIST);
                    }
                }catch (Exception e){
                    return ResultUtil.error(ResultEnum.HASEXIST);
                }
            }else {
                return ResultUtil.error(ResultEnum.NULLUSERID);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerException("注册失败");
        }
    }

    /**
     * 查找注册用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public Result findRegister(String userId) {
        try{
            if (isExistRegister(userId,"")==0) {
                return ResultUtil.success(registerUserDao.findRegister(userId));
            }else {
                return ResultUtil.error(ResultEnum.NOREGISTER);
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerException("查找注册用户失败");
        }
    }

    @Override
    public Result deleteRegisterByUserId(String userId) {
        ServiceUtil.deleteSuccess(registerUserDao.deleteRegisterByUserId(userId));
        return ResultUtil.success();
    }

    @Override
    public Result addApprovedUser(RegisterUser registerUser) {
        ServiceUtil.insertSuccess(loginUserDao.addApprovedUser(registerUser));
        return null;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public Result findRegisterByUserId(String userId) {
        try{
            return ResultUtil.success(loginUserDao.findRegisterByUserId(userId));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerException("查找注册用户失败");
        }
    }

    @Override
    public Result findApprovedUserByUserId(String userId) {
        try{
            return ResultUtil.success(loginUserDao.findApprovedUserByUserId(userId));
        }catch (Exception e){
            e.printStackTrace();
            throw new ServerException("查找审批用户失败");
        }
    }

    /**
     * 判断审批用户是否存在
     *
     * @param userId
     * @param password
     * @return
     * 1. 该用户已通过审批并且密码输入正确
     * 0. 该用户通过审批但密码输入错误
     * -1. 没有通过审批
     */
    @Override
    public int isExistApprovedUser(String userId, String password) {
        ApprovedUser approvedUser = loginUserDao.findApprovedUserByUserId(userId);
        if (approvedUser!=null){
            if(password.equals(approvedUser.getPassword())){
                return 1;
            }else {
                return 0;
            }
        }else {
            return -1;
        }
    }

    /**
     * 判断注册用户是否存在
     *
     * @param userId
     * @param password
     * @return
     * 1. 该用户已注册并且密码输入正确
     * 0. 该用户注册但密码输入错误
     * -1. 查询不到相关信息
     */
    @Override
    public int isExistRegister(String userId, String password) {
        RegisterUser registerUser = registerUserDao.findRegister(userId);
        if (registerUser!=null){
            if(password.equals(registerUser.getPassword())){
                return 1;
            }else {
                return 0;
            }
        }else {
            return -1;
        }
    }

    @Override
    public Boolean isExistGameGroup(String groupName) {
        GameGroupInfo gameGroupInfo = loginUserDao.findGameGroupByGroupName(groupName);
        if (gameGroupInfo != null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public Boolean isGroupMember(String userId) {
        GameGroupMemberInfo groupMember = loginUserDao.findGameGroupMemberByUserId(userId);
        return groupMember != null;
    }

    @Override
    public Boolean isGameRunning(String userId) {
        GameGroupMemberInfo groupMember = loginUserDao.findUserUniqueInGroupMemberByUserId(userId);
        if(groupMember!=null){
            return groupMember.getUserUnique() != null && groupMember.getCurrentPeriod() != 0 && groupMember.getStatus() > -1;
        }
        return false;
    }

    @Override
    public Boolean isGameCreator(String userId) {
        int count = loginUserDao.findGameCreatorByUserId(userId);
        return count > 0;
    }

    @Override
    public Result updatePassword(String userId, String password) {
        RegisterUser registerUser = registerUserDao.findRegister(userId);
        if (registerUser != null) {
            ServiceUtil.updateSuccess(commonDao.updateRegisterUserPassword(userId,password));
            return ResultUtil.success();
        }else {
            ApprovedUser approvedUser = loginUserDao.findApprovedUserByUserId(userId);
            if (approvedUser != null){
                ServiceUtil.updateSuccess(commonDao.updateApprovedUserPassword(userId,password));
                return ResultUtil.success();
            }else {
                return ResultUtil.error("更改密码失败");
            }
        }
    }

    /**
     * 用户登录
     *
     * @param userId
     * @param password
     * @return
     * 		1：已经  注册 && 未审核 的用户，并且 密码正确
     * 		2：已经 (注册 && 未审核) || 已经审核 的用户，但是 密码不正确
     * 		3：已经 登录 的用户，但是 没有加入分组
     * 		4：已经 加入游戏，但是已经 破产
     * 		5：已经 加入游戏，并且 正在比赛
     * 		6：已经 加入游戏，并且已经 比赛完毕
     * 		7：已经 加入一个分组，并且 是 游戏创建者
     * 		8：已经 加入一个分组，但是 不是 游戏创建者
     * 		-1：用户没有注册
     * 		-1000：非法状态
     */
    @Override
    public int login(String userId, String password, HttpServletRequest request, HttpServletResponse response) {
        int result;
        HttpSession session = request.getSession();
        if (session.getAttribute("userInfo") == null) {
            int approvedStatus = isExistApprovedUser(userId, password);
            if (approvedStatus == 0) {
                //密码错误
                result = 2;
            } else if (approvedStatus == -1) {
                //未审批or不存在
                int registerStatus = isExistRegister(userId, password);
                if (registerStatus == 1) {
                    //已经注册的用户.并且密码正确.
                    result = 1;
                } else if (registerStatus == 0) {
                    //已经注册的用户,但是密码不正确
                    result = 2;
                } else {
                    //用户未注册
                    result = -1;
                }
            } else {
                //已审批的用户，密码正确
                //判断游戏状态
                if (!isGroupMember(userId)) {
                    //未加入分组
                    result = 3;
                } else {
                    //已经开始比赛
                    if (isGameRunning(userId)) {
                        String uesrUnique = commonDao.findUserUniqueByUserId(userId);
                        int gameStatus = loginUserDao.getStatusByUserUnique(uesrUnique);

                        //ps：前端在结束经营后再次登陆返回的status为4，这个4是在这里生成的，前提条件是gamegroupmember中的Status是0（已破产）
                        if (gameStatus == 0) {
                            //已经破产
                            result = 4;
                        } else if (gameStatus == 1) {
                            //正在比赛中
                            result = 5;
                        } else if (gameStatus == 2) {
                            //已经完成了比赛
                            result = 6;
                        } else {
                            //不存在这个status状态.报错显示.
                            return -1000;
                        }
                    } else {
                        //比赛未开始
                        if (isGameCreator(userId)) {
                            //用户是创建者
                            result = 7;
                        } else {
                            //用户不是创建者
                            result = 8;
                        }
                    }
                }
            }
        }else {
            UserPo userPo = (UserPo) session.getAttribute("userInfo");
            result = userPo.getStatus();
        }
        return result;
    }

    //TODO: 添加-1000code，判断是否授权  2020.7.29王永、刘岽、任崇伟、孙鹏翔讨论后面处理。
    @Override
    public LoginResponse loginResponse(int loginStatus, String userId, HttpServletRequest request, HttpServletResponse response) {
        UserPo loginUser = new UserPo();
        HttpSession session = request.getSession(false);
        if (loginStatus == 2 || loginStatus == -1) {
        } else {
            session.setAttribute("userId", userId);
            if (loginStatus == 1) {
                UserPo registerUserPo = loginUserDao.loginRegisterUser(userId);
                registerUserPo.setStatus(loginStatus);
                registerUserPo.setRole("注册用户");
                session.setAttribute("userInfo", registerUserPo);
                loginUser = registerUserPo;
            } else {
                UserPo approvedUserPo = loginUserDao.loginApprovedUser(userId);
                approvedUserPo.setStatus(loginStatus);
                session.setAttribute("userInfo", approvedUserPo);
                loginUser = approvedUserPo;
                String userUnique = commonDao.findUserUniqueByUserId(userId);
                String userName = commonDao.findUserNameByUserId(userId);
                String groupName = commonDao.findGameGroupNameByUserId(userId);
                session.setAttribute("userUnique", userUnique);
                session.setAttribute("userName", userName);
                session.setAttribute("groupName", groupName);
                // 问题2修复：toString()前先判空（或用String.valueOf）
                log.info("用户名：{}", String.valueOf(session.getAttribute("userName")));
                log.info("用户唯一标识：{}", String.valueOf(session.getAttribute("userUnique")));
              }
        }
        String groupName = (String)session.getAttribute("groupName");
        Map<String, Object> map = new HashMap<>();
        map.put("userMessage",loginUser);
        switch (loginStatus) {
            case 1:
                return new LoginResponse<>(LoginCode.REGISTERUSER, LoginMessage.REGISTERUSER, map);
            case 2:
                return new LoginResponse<>(LoginCode.WRONG_PASSWORD, LoginMessage.WRONG_PASSWORD, null);
            case 3:
                session.setAttribute("groupName","NoGroup");
                List<GameGroupInfo> groupList = groupManageDao.getGroupList();
                map.put("groupList",groupList);
                return new LoginResponse<>(LoginCode.NO_GAMEGROUP, LoginMessage.NO_GAMEGROUP, map);
            case 4:
                return new LoginResponse<>(LoginCode.RUIN, LoginMessage.RUIN, map);
            case 5:
                return new LoginResponse<>(LoginCode.IN_GAME, LoginMessage.IN_GAME, map);
            case 6:
                return new LoginResponse<>(LoginCode.GAME_COMPLETE, LoginMessage.GAME_COMPLETE, map);
            case 7:
                GameGroupInfo gameGroupInfo_creator = loginUserDao.findGameGroupByGroupName(groupName);
                map.put("groupMessage",gameGroupInfo_creator);
                List<GameGroupMemberInfo> gameGroupMemberInfoList_creator = loginUserDao.findGroupMembersByGroupName(groupName);
                map.put( "groupMemberList",gameGroupMemberInfoList_creator);
                return new LoginResponse<>(LoginCode.GAMEGROUP_CREATOR, LoginMessage.GAMEGROUP_CREATOR, map);
            case 8:
                GameGroupInfo gameGroupInfo_member = loginUserDao.findGameGroupByGroupName(groupName);
                map.put("groupMessage",gameGroupInfo_member);
                List<GameGroupMemberInfo> gameGroupMemberInfoList_member = loginUserDao.findGroupMembersByGroupName(groupName);
                map.put( "groupMemberList",gameGroupMemberInfoList_member);
                return new LoginResponse<>(LoginCode.GAMEGROUP_MEMBER, LoginMessage.GAMEGROUP_MEMBER, map);
            case -1:
                return new LoginResponse<>(LoginCode.NOT_REGISTER, LoginMessage.NOT_REGISTER, null);
            default:
                return new LoginResponse<>(LoginCode.ILLEGAL, LoginMessage.ILLEGAL, null);
        }
    }



    @Override
    public Result modifyUser(UserStudentInfoRegisterDto studentInfoRegisterDto) {
        Map<String, Object> map = new HashMap<String, Object>();
        RegisterUser registerUser = registerUserDao.findRegister(studentInfoRegisterDto.getUserId());
        if (registerUser != null) {
            ServiceUtil.updateSuccess(registerUserDao.modifyRegisterUser(studentInfoRegisterDto.getUserId(),studentInfoRegisterDto.getUserName(),studentInfoRegisterDto.getPassword(),studentInfoRegisterDto.getMajor(),studentInfoRegisterDto.getSClass(),studentInfoRegisterDto.getSId(),studentInfoRegisterDto.getEmail(),studentInfoRegisterDto.getTel(),studentInfoRegisterDto.getGender()));
            map.put("userMessage",registerUser);
            return ResultUtil.success(map);
        }else {
            ApprovedUser approvedUser = loginUserDao.findApprovedUserByUserId(studentInfoRegisterDto.getUserId());
            if (approvedUser != null){
                ServiceUtil.updateSuccess(loginUserDao.modifyApprovedUser(studentInfoRegisterDto.getUserId(),studentInfoRegisterDto.getUserName(),studentInfoRegisterDto.getPassword(),studentInfoRegisterDto.getMajor(),studentInfoRegisterDto.getSClass(),studentInfoRegisterDto.getSId(),studentInfoRegisterDto.getEmail(),studentInfoRegisterDto.getTel(),studentInfoRegisterDto.getGender()));
                map.put("userMessage",approvedUser);
                return ResultUtil.success(map);
            }else {
                return ResultUtil.error("更改用户信息失败");
            }
        }
    }

    /**
     * @Description
     * 	这是一个用来支持用户登录后用来恢复用户退出时的状态的一个方法
     * 	方法的主体内容是判断用户退出时属于什么状态
     * 		1.方法会在待审核表当中去查找该用户是否是待审核状态
     * 			1）是：返回 1
     * 			2）否：程序继续运行
     * 		2.判断用户是否已经审核通过
     * 			1）是：程序继续运行
     * 			2）否：返回 -404
     * 		3.判断用户的身份
     * 			1）创建了游戏，返回 3
     * 			2）是游戏的成员，返回 4
     * 			3）参与的游戏正在运行，返回 5
     * 			4）以上三种情况都没有，返回 2
     * @param userId
     * @return
     * 		1 处于待审核状态
     * 		2 用户已经被授权，且没有加入游戏分组
     * 		3 用户创建了一个游戏分组
     * 		4 用户加入了一个游戏分组
     * 		5 用户加入的游戏正在比赛
     */
    @Override
    public int loginStatus(String userId) {
        if(isExistRegister(userId,"")!=-1){
            //是注册用户.
            return 1;
        }else{
            if(isExistApprovedUser(userId,"")!=-1){
                if(isGameCreator(userId)){
                    return 3;
                }else if(isGroupMember(userId)){
                    return 4;
                }else if(isGameRunning(userId)){
                    return 5;
                }
                return 2;
            }
        }
        return -404;//出错.
    }

    @Override
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        try{
            HttpSession session = request.getSession();
            Object userInfo = session.getAttribute("userInfo");
            if(userInfo == null){
                return ResultUtil.error("注销失败，未登录");
            }else {
                session.removeAttribute("userInfo");
                session.removeAttribute("userId");
                return ResultUtil.success("注销成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResultUtil.error("注销失败，未知错误");
        }
    }

//    TODO 生产线+厂房 理一理需求
    @Override
    public Result getUserInfo(String userUnique) {
        if (userUnique != null) {
            List<FactoryUsing> allFactoryUsing = factoryUsingDao.listFactoryUsingsByUserUnique(userUnique);
            TaxSheet tax = taxSheetDao.findTaxSheetByUserUnique(userUnique);
            Double cash = cashDao.findCash(userUnique);
            String gameGroup = commonDao.findGameGroupNameByUserUnique(userUnique);
            Integer currentPeriod = commonDao.findCurrentPeriod(userUnique);
            Integer currentYear = (currentPeriod-1) / 4 + 1;
            Integer realPeriod = currentPeriod - (currentYear - 1) * 4;
            String userId = commonDao.findUserIdByUserUnique(userUnique);
            UserPo approvedUser = loginUserDao.loginApprovedUser(userId);
            UserInfoBeginGame result = UserInfoBeginGame.builder()
                    .userName(userId)
                    .years(currentYear)
                    .currentPeriod(realPeriod)
                    .cash(cash)
                    .factoryCount(allFactoryUsing.size())
                    .groupName(gameGroup)
                    .tax(tax.getTax())
                    .userUnique(userUnique)
                    .gender(approvedUser.getGender())
                    .build();
            return ResultUtil.success(result);
        }
        log.error("userUnique为空");
        return ResultUtil.error();

    }


    /**
     * 重新开始游戏
     *
     * @param userUnique 用户唯一标识符
     * @return 0 - 成功；
     * 1 - 失败
     */
    @Override
    public Result replay(String userUnique) {
        try {
            if (!StringUtils.isEmpty(userUnique)){

                //-1表示用户结束经营，退出了比赛，对该组的比赛将不再产生影响
                if(groupManageDao.updateBankruptcyUserStatus(userUnique, -1)) {
                    return ResultUtil.success();
                }

                log.error("update失败");
                return ResultUtil.error();
            }

            log.error("参数为空");
            return ResultUtil.error("参数为空");
        }catch (Exception e){
            log.error("重新开始游戏失败 :{}", e.getMessage());
            return ResultUtil.error();
        }
    }
}
