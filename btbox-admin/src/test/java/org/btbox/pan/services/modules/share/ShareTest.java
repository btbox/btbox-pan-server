package org.btbox.pan.services.modules.share;

import cn.hutool.core.lang.Assert;
import org.assertj.core.util.Lists;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
import org.btbox.pan.services.modules.share.domain.context.QueryShareListContext;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;
import org.btbox.pan.services.modules.share.enums.ShareDayTypeEnum;
import org.btbox.pan.services.modules.share.enums.ShareTypeEnum;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;
import org.btbox.pan.services.modules.user.domain.vo.UserInfoVO;
import org.btbox.pan.services.modules.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.btbox.pan.services.modules.user.UserTest.*;
import static org.btbox.pan.services.modules.user.UserTest.ANSWER;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 14:51
 * @version: 1.0
 */
@SpringBootTest
@Transactional
public class ShareTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFileService userFileService;

    @Autowired
    private PanShareService panShareService;


    /**
     * 创建分享链接成功
     */
    @Test
    public void createShareUrlSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        PanShareUrlVO vo = panShareService.create(createShareUrlContext);
        Assert.isTrue(Objects.nonNull(vo));
    }

    /**
     * 查询分享链接列表成功
     */
    @Test
    public void queryShareUrlListSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        CreateShareUrlContext createShareUrlContext = new CreateShareUrlContext();
        createShareUrlContext.setShareName("share-1");
        createShareUrlContext.setShareDayType(ShareDayTypeEnum.SEVEN_DAYS_VALIDITY.getCode());
        createShareUrlContext.setShareType(ShareTypeEnum.NEED_SHARE_CODE.getCode());
        createShareUrlContext.setUserId(userId);
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(fileId));
        PanShareUrlVO vo = panShareService.create(createShareUrlContext);
        Assert.isTrue(Objects.nonNull(vo));

        QueryShareListContext queryShareListContext = new QueryShareListContext();
        queryShareListContext.setUserId(userId);
        List<PanShareUrlListVO> result = panShareService.getShares(queryShareListContext);
        Assert.notEmpty(result);
    }

    private Long register() {
        UserRegisterContext context = createUserRegisterContext();
        Long userId = userService.register(context);
        Assert.isTrue(userId > 0L);
        return userId;
    }

    private UserInfoVO info(Long userId) {
        UserInfoVO info = userService.info(userId);
        Assert.notNull(info);
        return info;
    }

    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

}