package org.btbox.pan.services.modules.user.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.common.core.utils.PasswordUtil;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.user.domain.context.UserLoginContext;
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.repository.mapper.BtboxPanUserMapper;
import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.lang.constant.ConstantDesc;

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


    /************************************private****************************/

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