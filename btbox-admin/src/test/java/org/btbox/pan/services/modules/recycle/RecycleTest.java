package org.btbox.pan.services.modules.recycle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.enums.MergeFlagEnum;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import org.btbox.pan.services.modules.file.domain.vo.*;
import org.btbox.pan.services.modules.file.service.PanFileChunkService;
import org.btbox.pan.services.modules.file.service.PanFileService;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.recycle.domain.context.QueryRecycleFileListContext;
import org.btbox.pan.services.modules.recycle.domain.context.RestoreContext;
import org.btbox.pan.services.modules.recycle.service.RecycleService;
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;
import org.btbox.pan.services.modules.user.domain.vo.UserInfoVO;
import org.btbox.pan.services.modules.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.btbox.pan.services.modules.user.UserTest.*;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 11:20
 * @version: 1.0
 */
@SpringBootTest
@Transactional
public class RecycleTest {

    @Autowired
    private UserFileService userFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private PanFileService panFileService;

    @Autowired
    private PanFileChunkService panFileChunkService;

    @Autowired
    private RecycleService recycleService;


    /**
     * 测试查询回收站文件列表成功
     */
    @Test
    public void testQueryRecyclesSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 查询回收站列表，校验列表的长度为1
        QueryRecycleFileListContext queryRecycleFileListContext = new QueryRecycleFileListContext();
        queryRecycleFileListContext.setUserId(userId);
        List<UserFileVO> recycles = recycleService.recycles(queryRecycleFileListContext);

        Assert.isTrue(CollUtil.isNotEmpty(recycles) && recycles.size() == 1);
    }

    /**
     * 文件还原成功测试
     */
    @Test
    public void testFileRestoreSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        // 创建一个文件夹
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        // 删掉该文件夹
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);
        userFileService.deleteFile(deleteFileContext);

        // 文件还原
        RestoreContext restoreContext = new RestoreContext();
        restoreContext.setUserId(userId);
        restoreContext.setFileIdList(Lists.newArrayList(fileId));
        recycleService.restore(restoreContext);
    }

    /**
     * 测试文件还原失败-错误的用户ID
     */
    @Test
    public void testFileRestoreFailByWrongUserId() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            // 创建一个文件夹
            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-old");

            Long fileId = userFileService.createFolder(context);
            Assert.notNull(fileId);

            // 删掉该文件夹
            DeleteFileContext deleteFileContext = new DeleteFileContext();
            List<Long> fileIdList = Lists.newArrayList();
            fileIdList.add(fileId);
            deleteFileContext.setFileIdList(fileIdList);
            deleteFileContext.setUserId(userId);
            userFileService.deleteFile(deleteFileContext);

            // 文件还原
            RestoreContext restoreContext = new RestoreContext();
            restoreContext.setUserId(userId + 1);
            restoreContext.setFileIdList(Lists.newArrayList(fileId));
            recycleService.restore(restoreContext);
        });
    }


    /**
     * 测试文件还原失败-错误的文件名称
     */
    @Test
    public void testFileRestoreFailByWrongFilename1() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            // 创建一个文件夹
            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-1");

            Long fileId = userFileService.createFolder(context);
            Assert.notNull(fileId);

            // 删掉该文件夹
            DeleteFileContext deleteFileContext = new DeleteFileContext();
            List<Long> fileIdList = Lists.newArrayList();
            fileIdList.add(fileId);
            deleteFileContext.setFileIdList(fileIdList);
            deleteFileContext.setUserId(userId);
            userFileService.deleteFile(deleteFileContext);

            context.setFolderName("folder-name-1");
            fileId = userFileService.createFolder(context);
            Assert.notNull(fileId);

            // 文件还原
            RestoreContext restoreContext = new RestoreContext();
            restoreContext.setUserId(userId);
            restoreContext.setFileIdList(Lists.newArrayList(fileId));
            recycleService.restore(restoreContext);
        });

    }

    /**
     * 测试文件还原失败-错误的文件名称
     */
    @Test
    public void testFileRestoreFailByWrongFilename2() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            // 创建一个文件夹
            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-1");
            Long fileId1 = userFileService.createFolder(context);
            Assert.notNull(fileId1);

            // 删掉该文件夹
            DeleteFileContext deleteFileContext = new DeleteFileContext();
            List<Long> fileIdList = Lists.newArrayList();
            fileIdList.add(fileId1);
            deleteFileContext.setFileIdList(fileIdList);
            deleteFileContext.setUserId(userId);
            userFileService.deleteFile(deleteFileContext);

            context.setFolderName("folder-name-1");
            Long fileId2 = userFileService.createFolder(context);
            Assert.notNull(fileId2);

            fileIdList.add(fileId2);
            userFileService.deleteFile(deleteFileContext);

            // 文件还原
            RestoreContext restoreContext = new RestoreContext();
            restoreContext.setUserId(userId);
            restoreContext.setFileIdList(Lists.newArrayList(fileId1, fileId2));
            recycleService.restore(restoreContext);
        });
    }


    /*********************************************** private ******************************************/


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