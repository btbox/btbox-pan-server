package org.btbox.pan.services.modules.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.constant.UserConstants;
import org.btbox.common.core.enums.ResponseCode;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.*;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.user.convert.UserConvert;
import org.btbox.pan.services.modules.user.domain.context.*;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.domain.vo.UserInfoVO;
import org.btbox.pan.services.modules.user.repository.mapper.BtboxPanUserMapper;
import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.lang.constant.ConstantDesc;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/13 16:53
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<BtboxPanUserMapper, BtboxPanUser> implements UserService {

    private final UserFileService userFileService;

    private final UserConvert userConvert;

    
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        // 转换实体
        assembleUserEntity(userRegisterContext);
        // 注册
        doRegister(userRegisterContext);
        //
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * 需要实现的功能：
     * 1、用户的登录信息校验
     * 2、生成一个具有时效性的accessToken
     * 3、将accessToken缓存起来，时去实现单机登录
     * @param userLoginContext
     * @return
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);
        generateAndSaveAccessToken(userLoginContext);
        return userLoginContext.getAccessToken();
    }

    @Override
    public void exit() {
        try {
            StpUtil.logout();
        } catch (Exception e) {
            throw new ServiceException(MessageUtils.message("user.unknown.error"));
        }
    }

    @Override
    public String checkUsername(CheckUsernameContext checkUsernameContext) {
        LambdaQueryWrapper<BtboxPanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(BtboxPanUser::getQuestion);
        queryWrapper.eq(BtboxPanUser::getUsername, checkUsernameContext.getUsername());
        BtboxPanUser user = this.getOne(queryWrapper);
        if (ObjectUtil.isEmpty(user)) {
            throw new ServiceException(MessageUtils.message("user.not.exists", checkUsernameContext.getUsername()));
        }
        return user.getQuestion();
    }

    @Override
    public String checkAnswer(CheckAnswerContext context) {
        LambdaQueryWrapper<BtboxPanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BtboxPanUser::getUsername, context.getUsername());
        queryWrapper.eq(BtboxPanUser::getQuestion, context.getQuestion());
        queryWrapper.eq(BtboxPanUser::getAnswer, context.getAnswer());

        long count = this.count(queryWrapper);
        if (count == 0) {
            throw new ServiceException(MessageUtils.message("user.answer.error"));
        }

        return generateAndSaveCheckAnswerToken(context);
    }

    /**
     * 重置用户密码
     * 1. 校验token是否有效
     * 2. 重置密码
     * @param context
     */
    @Override
    public void resetPassword(ResetPasswordContext context) {
        checkForgetPasswordToken(context);
        checkAndResetUserPassword(context);
    }

    /**
     * 在线修改密码
     * 1. 校验旧密码
     * 2. 重置新密码
     * 3. 退出当前的登录状态
     * @param context
     */
    @Override
    public void changePassword(ChangePasswordContext context) {
        checkOldPassword(context);
        doChangePassword(context);
        exitLoginStatus(context);
    }

    /**
     * 查询在线用户的基本信息
     * 1. 查询用户的基本信息实体
     * 2. 查询用户的根文件夹信息
     * 3. 拼装VO对象返回
     * @param userId 用户id
     * @return
     */
    @Override
    public UserInfoVO info(Long userId) {

        BtboxPanUser entity = this.getById(userId);
        if (ObjectUtil.isEmpty(entity)) {
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }

        UserFile userRootFileInfo = getUserRootFileInfo(userId);
        if (ObjectUtil.isEmpty(userRootFileInfo)) {
            throw new ServiceException("查询用户根文件夹信息失败");
        }

        return userConvert.assembleUserInfoVO(entity, userRootFileInfo);
    }

    /**
     * 获取用户根文件夹信息实体
     * @param userId
     */
    private UserFile getUserRootFileInfo(Long userId) {
        return userFileService.getUserRootFile(userId);
    }

    /**
     * 退出用户的登录状态
     * @param context
     */
    private void exitLoginStatus(ChangePasswordContext context) {
        StpUtil.logout();
    }

    /**
     * 修改新密码
     * @param context
     */
    private void doChangePassword(ChangePasswordContext context) {
        String newPassword = context.getNewPassword();
        BtboxPanUser entity = context.getEntity();
        // 加密新密码
        String encNewPassword = PasswordUtil.encrypt(newPassword);
        entity.setPassword(encNewPassword);

        if (!this.updateById(entity)) {
            throw new ServiceException(MessageUtils.message("user.change.password.error"));
        }

    }

    /**
     * 校验用户的旧密码
     * 查询并封装用户的实体信息到上下文对象中
     * @param context
     */
    private void checkOldPassword(ChangePasswordContext context) {
        // 设置用户id
        context.setUserId(StpUtil.getLoginIdAsLong());
        String oldPassword = context.getOldPassword();

        BtboxPanUser entity = this.getById(context.getUserId());
        if (ObjectUtil.isEmpty(entity)) {
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        context.setEntity(entity);

        // 校验数据库密码和当前用户输入的旧密码是否一致
        boolean decrypt = PasswordUtil.decrypt(oldPassword, entity.getPassword());
        if (!decrypt) {
            throw new ServiceException(MessageUtils.message("user.old.password.verify.error"));
        }
    }


    /************************************private****************************/

    /**
     * 校验用户和重置密码
     * @param context
     */
    private void checkAndResetUserPassword(ResetPasswordContext context) {
        String username = context.getUsername();
        String password = context.getPassword();
        BtboxPanUser entity = getUserByUsername(username);
        if (ObjectUtil.isEmpty(entity)) {
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        String newDBPassword = PasswordUtil.encrypt(password);
        entity.setPassword(newDBPassword);

        if (!this.updateById(entity)) {
            throw new ServiceException(MessageUtils.message("user.password.reset.error"));
        }

    }

    /**
     * 验证忘记密码的token是否有效
     * @param context
     */
    private void checkForgetPasswordToken(ResetPasswordContext context) {
        String token = context.getToken();
        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USERNAME);
        // token不存在
        if (ObjectUtil.isEmpty(value)) {
            throw new ServiceException(ResponseCode.TOKEN_EXPIRE.getDesc(), ResponseCode.TOKEN_EXPIRE.getCode());
        }

        // token值不一致
        if (!StringUtils.equals(value.toString(), context.getUsername())) {
            throw new ServiceException("token错误");
        }
    }


    /**
     * 生成用户忘记密码-校验密保答案通过的临时token
     * @param context
     * @return
     */
    private String generateAndSaveCheckAnswerToken(CheckAnswerContext context) {
        return JwtUtil.generateToken(context.getUsername(), UserConstants.FORGET_USERNAME, context.getUsername(), UserConstants.FIVE_MINUTES_LONG);
    }

    /**
     * 生成并保存登录之后的凭证
     * @param userLoginContext
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        BtboxPanUser entity = userLoginContext.getEntity();
        // 登录
        StpUtil.login(entity.getUserId());
        // 获取token值
        userLoginContext.setAccessToken(StpUtil.getTokenValue());
    }

    /**
     * 校验用户密码
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        BtboxPanUser entity = getUserByUsername(username);
        if (ObjectUtil.isEmpty(entity)) {
            throw new ServiceException("用户名不存在");
        }
        userLoginContext.setEntity(entity);
        String dbPassword = entity.getPassword();
        boolean checkPassword = PasswordUtil.decrypt(password, dbPassword);
        if (!checkPassword) {
            throw new ServiceException("密码不正确");
        }
    }

    /**
     * 通过用户名称获取用户实体信息
     * @param username
     * @return
     */
    private BtboxPanUser getUserByUsername(String username) {
        LambdaQueryWrapper<BtboxPanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BtboxPanUser::getUsername, username);
        return this.getOne(queryWrapper);
    }

    /**
     * 实体转化
     * 由上下文信息转化成用户实体，封装进上下文
     *
     * @param userRegisterContext
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        BtboxPanUser entity = MapstructUtils.convert(userRegisterContext, BtboxPanUser.class);
        entity.setUserId(IdWorker.getId());
        entity.setPassword(PasswordUtil.encrypt(userRegisterContext.getPassword()));
        userRegisterContext.setEntity(entity);
    }

    /**
     * 实现注册用户的业务
     * 捕获数据库唯一索引冲突异常，来实现全局用户名称唯一
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {

        BtboxPanUser entity = userRegisterContext.getEntity();
        // 不存在则注册保存
        if (ObjectUtil.isNotEmpty(entity)) {
            try {
                if (!this.save(entity)) {
                    throw new ServiceException(MessageUtils.message("user.register.error"));
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new ServiceException(MessageUtils.message("user.username.has"));
            }
        } else {
            throw new ServiceException("注册出现异常");
        }
    }

    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        userFileService.createFolder(createFolderContext);
    }
}