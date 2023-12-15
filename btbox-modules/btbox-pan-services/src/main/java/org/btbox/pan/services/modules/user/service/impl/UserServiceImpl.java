package org.btbox.pan.services.modules.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
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
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.repository.mapper.BtboxPanUserMapper;
import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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