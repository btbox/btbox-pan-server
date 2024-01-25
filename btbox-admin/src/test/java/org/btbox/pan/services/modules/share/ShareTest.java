package org.btbox.pan.services.modules.share;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import org.assertj.core.util.Lists;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.share.convert.CancelShareContext;
import org.btbox.pan.services.modules.share.domain.context.*;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;
import org.btbox.pan.services.modules.share.domain.vo.ShareDetailVO;
import org.btbox.pan.services.modules.share.domain.vo.ShareSimpleDetailVO;
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

    /**
     * 取消分享成功
     */
    @Test
    public void cancelShareSuccess() {
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

        CancelShareContext cancelShareContext = new CancelShareContext();
        cancelShareContext.setUserId(userId);
        cancelShareContext.setShareIdList(Lists.newArrayList(vo.getShareId()));
        panShareService.cancelShare(cancelShareContext);

        result = panShareService.getShares(queryShareListContext);
        Assert.isTrue(CollUtil.isEmpty(result));
    }

    /**
     * 校验分享码成功
     */
    @Test
    public void checkShareCodeSuccess() {
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

        CheckShareCodeContext checkShareCodeContext = new CheckShareCodeContext();
        checkShareCodeContext.setShareId(vo.getShareId());
        checkShareCodeContext.setShareCode(vo.getShareCode());
        String token = panShareService.checkShareCode(checkShareCodeContext);
        Assert.notBlank(token);
    }

    /**
     * 校验查询分享详情成功
     */
    @Test
    public void queryShareDetailSuccess() {
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

        QueryShareDetailContext queryShareDetailContext = new QueryShareDetailContext();
        queryShareDetailContext.setShareId(vo.getShareId());
        ShareDetailVO shareDetailVO = panShareService.detail(queryShareDetailContext);
        Assert.notNull(shareDetailVO);
    }

    /**
     * 校验查询分享简单详情成功
     */
    @Test
    public void queryShareSimpleDetailSuccess() {
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

        QueryShareSimpleDetailContext queryShareSimpleDetailContext = new QueryShareSimpleDetailContext();
        queryShareSimpleDetailContext.setShareId(vo.getShareId());
        ShareSimpleDetailVO shareSimpleDetailVO = panShareService.simpleDetail(queryShareSimpleDetailContext);
        Assert.notNull(shareSimpleDetailVO);
    }

    /**
     * 校验查询分享下一级文件列表成功
     */
    @Test
    public void queryShareFileListSuccess() {
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
        createShareUrlContext.setShareFileIdList(Lists.newArrayList(userInfoVO.getRootFileId()));
        PanShareUrlVO vo = panShareService.create(createShareUrlContext);
        Assert.isTrue(Objects.nonNull(vo));

        QueryChildFileListContext queryChildFileListContext = new QueryChildFileListContext();
        queryChildFileListContext.setShareId(vo.getShareId());
        queryChildFileListContext.setParentId(userInfoVO.getRootFileId());
        List<UserFileVO> fileVOList = panShareService.fileList(queryChildFileListContext);
        Assert.notEmpty(fileVOList);
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