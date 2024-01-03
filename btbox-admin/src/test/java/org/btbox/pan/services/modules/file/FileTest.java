package org.btbox.pan.services.modules.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.enums.MergeFlagEnum;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import org.btbox.pan.services.modules.file.domain.vo.FileChunkUploadVO;
import org.btbox.pan.services.modules.file.domain.vo.FolderTreeNodeVO;
import org.btbox.pan.services.modules.file.domain.vo.UploadedChunksVO;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.PanFileChunkService;
import org.btbox.pan.services.modules.file.service.PanFileService;
import org.btbox.pan.services.modules.file.service.UserFileService;
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
public class FileTest {
    
    @Autowired
    private UserFileService userFileService;

    @Autowired
    private UserService userService;

    @Autowired
    private PanFileService panFileService;

    @Autowired
    private PanFileChunkService panFileChunkService;

    /**
     * 测试用户查询文件列表成功
     */
    @Test
    public void testQueryUserFileListSuccess() {
        Long userId = register();
        UserInfoVO info = info(userId);

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(info.getRootFileId());
        context.setUserId(userId);
        context.setFileTypeArray(null);
        context.setDelFlag(DelFlagEnum.NO.getCode());

        List<UserFileVO> fileList = userFileService.getFileList(context);
        Assert.isTrue(CollUtil.isEmpty(fileList));
    }

    /**
     * 测试创建文件夹成功
     */
    @Test
    public void testCreateFolderSuccess() {
        Long userId = register();
        UserInfoVO info = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(info.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);
    }

    /**
     * 修改文件名称成功
     */
    @Test
    public void testUpdateFilenameSuccess() {
        Long userId = register();
        UserInfoVO info = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(info.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("new-folder-name");

        userFileService.updateFilename(updateFilenameContext);

    }

    /**
     * 校验文件删除失败-非法的文件ID
     */
    @Test
    public void testDeleteFileFailByWrongFileId() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-old");

            Long fileId = userFileService.createFolder(context);
            Assert.notNull(fileId);

            DeleteFileContext deleteFileContext = new DeleteFileContext();
            List<Long> fileIdList = Lists.newArrayList();
            fileIdList.add(fileId + 1);
            deleteFileContext.setFileIdList(fileIdList);
            deleteFileContext.setUserId(userId);

            userFileService.deleteFile(deleteFileContext);
        });
    }

    /**
     * 校验文件删除失败-非法的用户ID
     */
    @Test
    public void testDeleteFileFailByWrongUserId() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-old");

            Long fileId = userFileService.createFolder(context);
            Assert.notNull(fileId);

            DeleteFileContext deleteFileContext = new DeleteFileContext();
            List<Long> fileIdList = Lists.newArrayList();
            fileIdList.add(fileId);
            deleteFileContext.setFileIdList(fileIdList);
            deleteFileContext.setUserId(userId + 1);

            userFileService.deleteFile(deleteFileContext);
        });
    }


    @Test
    public void testDeleteFileSuccess() {
        Long userId = register();
        UserInfoVO info = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(info.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");
        Long fileId = userFileService.createFolder(context);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);

        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 校验秒传文件成功
     */
    @Test
    public void testSecUploadSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        String identifier = "123456789";

        PanFile record = new PanFile();
        record.setFileId(IdUtil.get());
        record.setFilename("filename");
        record.setRealPath("realpath");
        record.setFileSize("fileSize");
        record.setFileSizeDesc("fileSizeDesc");
        record.setFilePreviewContentType("");
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        panFileService.save(record);

        SecUploadFileContext context = new SecUploadFileContext();
        context.setIdentifier(identifier);
        context.setFilename("filename");
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);

        boolean result = userFileService.secUpload(context);
        Assert.isTrue(result);
    }

    /**
     * 校验秒传文件失败
     */
    @Test
    public void testSecUploadFail() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        String identifier = "123456789";

        PanFile record = new PanFile();
        record.setFileId(IdUtil.get());
        record.setFilename("filename");
        record.setRealPath("realpath");
        record.setFileSize("fileSize");
        record.setFileSizeDesc("fileSizeDesc");
        record.setFilePreviewContentType("");
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        panFileService.save(record);

        SecUploadFileContext context = new SecUploadFileContext();
        context.setIdentifier(identifier + "_update");
        context.setFilename("filename");
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);

        boolean result = userFileService.secUpload(context);
        Assert.isFalse(result);
    }

    /**
     * 测试单文件上传成功
     */
    @Test
    public void testUploadSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        FileUploadContext context = new FileUploadContext();
        MultipartFile file = genarateMultipartFile();
        context.setFile(file);
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setIdentifier("12345678");
        context.setTotalSize(file.getSize());
        context.setFilename(file.getOriginalFilename());
        userFileService.upload(context);

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setUserId(userId);
        queryFileListContext.setParentId(userInfoVO.getRootFileId());
        List<UserFileVO> fileList = userFileService.getFileList(queryFileListContext);
        Assert.notEmpty(fileList);
        Assert.isTrue(fileList.size() == 1);
    }

    /**
     * 测试查询用户已上传的文件分片信息列表成功
     */
    @Test
    public void testQueryUploadedChunksSuccess() {
        Long userId = register();

        String identifier = "123456789";

        PanFileChunk record = new PanFileChunk();
        record.setId(IdUtil.get());
        record.setIdentifier(identifier);
        record.setRealPath("realPath");
        record.setChunkNumber(1);
        record.setExpirationTime(DateUtil.offsetDay(new Date(), 1));
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        boolean save = panFileChunkService.save(record);
        Assert.isTrue(save);

        QueryUploadedChunksContext context = new QueryUploadedChunksContext();
        context.setIdentifier(identifier);
        context.setUserId(userId);

        UploadedChunksVO vo = userFileService.getUploadedChunks(context);
        Assert.notNull(vo);
        Assert.notEmpty(vo.getUploadedChunks());

    }

    /**
     * 测试文件分片上传成功
     */
    @Test
    public void uploadWithChunkTest() throws InterruptedException {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new ChunkUploader(countDownLatch, i + 1, 10, userFileService, userId, userInfoVO.getRootFileId()).start();
        }
        countDownLatch.await();
    }

    /**
     * 测试查询文件夹树
     */
    @Test
    public void testGetFolderTreeNodeVOListTest() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        context.setFolderName("folder-name-2");
        context.setParentId(fileId);

        fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        context.setFolderName("folder-name-2-1");
        context.setParentId(fileId);

        fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        QueryFolderTreeContext queryFolderTreeContext = new QueryFolderTreeContext();
        queryFolderTreeContext.setUserId(userId);
        List<FolderTreeNodeVO> folderTree = userFileService.getFolderTree(queryFolderTreeContext);
        Assert.isTrue(folderTree.size() == 1);
        folderTree.forEach(FolderTreeNodeVO::print);

    }

    /**
     * 测试文件转移成功
     */
    @Test
    public void testTransferFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");

        Long folder1 = userFileService.createFolder(context);
        Assert.notNull(folder1);

        context.setFolderName("folder-name-2");
        Long folder2 = userFileService.createFolder(context);
        Assert.notNull(folder2);

        TransferFileContext transferFileContext = new TransferFileContext();
        transferFileContext.setTargetParentId(folder1);
        transferFileContext.setFileIdList(Lists.newArrayList(folder2));
        transferFileContext.setUserId(userId);
        userFileService.transfer(transferFileContext);

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setParentId(userInfoVO.getRootFileId());
        queryFileListContext.setUserId(userId);
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        List<UserFileVO> records = userFileService.getFileList(queryFileListContext);
        Assert.notEmpty(records);
    }

    /**
     * 测试文件转移失败，目标文件夹是要转移的文件列表中的文件夹或者是其子文件夹
     */
    @Test
    public void testTransferFileFail() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-1");

            Long folder1 = userFileService.createFolder(context);
            Assert.notNull(folder1);

            context.setParentId(folder1);
            context.setFolderName("folder-name-2");
            Long folder2 = userFileService.createFolder(context);
            Assert.notNull(folder2);

            TransferFileContext transferFileContext = new TransferFileContext();
            transferFileContext.setTargetParentId(folder2);
            transferFileContext.setFileIdList(Lists.newArrayList(folder1));
            transferFileContext.setUserId(userId);
            userFileService.transfer(transferFileContext);
        });
    }

    /**
     * 测试文件复制成功
     */
    @Test
    public void testCopyFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");

        Long folder1 = userFileService.createFolder(context);
        Assert.notNull(folder1);

        context.setFolderName("folder-name-2");
        Long folder2 = userFileService.createFolder(context);
        Assert.notNull(folder2);

        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setTargetParentId(folder1);
        copyFileContext.setFileIdList(Lists.newArrayList(folder2));
        copyFileContext.setUserId(userId);
        userFileService.copy(copyFileContext);

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setParentId(folder1);
        queryFileListContext.setUserId(userId);
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        List<UserFileVO> records = userFileService.getFileList(queryFileListContext);
        Assert.notEmpty(records);
    }

    /**
     * 测试文件复制失败，目标文件夹是要转移的文件列表中的文件夹或者是其子文件夹
     */
    @Test
    public void testCopyFileFail() {
        Assertions.assertThrows(ServiceException.class, () -> {
            Long userId = register();
            UserInfoVO userInfoVO = info(userId);

            CreateFolderContext context = new CreateFolderContext();
            context.setParentId(userInfoVO.getRootFileId());
            context.setUserId(userId);
            context.setFolderName("folder-name-1");

            Long folder1 = userFileService.createFolder(context);
            Assert.notNull(folder1);

            context.setParentId(folder1);
            context.setFolderName("folder-name-2");
            Long folder2 = userFileService.createFolder(context);
            Assert.notNull(folder2);

            CopyFileContext copyFileContext = new CopyFileContext();
            copyFileContext.setTargetParentId(folder2);
            copyFileContext.setFileIdList(Lists.newArrayList(folder1));
            copyFileContext.setUserId(userId);
            userFileService.copy(copyFileContext);
        });
    }


    /************************** private ********************/

    /**
     * 文件分片上传器
     */
    @AllArgsConstructor
    private static class ChunkUploader extends Thread {

        private CountDownLatch countDownLatch;

        private Integer chunk;

        private Integer chunks;

        private UserFileService userFileService;

        private Long userId;

        private Long parentId;

        /**
         * 1、上传文件分片
         * 2、根据上传的结果来调用文件分片合并
         */
        @Override
        public void run() {
            super.run();
            MultipartFile file = genarateMultipartFile();
            Long totalSize = file.getSize() * chunks;
            String filename = "test.txt";
            String identifier = "123456789";

            FileChunkUploadContext fileChunkUploadContext = new FileChunkUploadContext();
            fileChunkUploadContext.setFilename(filename);
            fileChunkUploadContext.setIdentifier(identifier);
            fileChunkUploadContext.setTotalChunks(chunks);
            fileChunkUploadContext.setChunkNumber(chunk);
            fileChunkUploadContext.setCurrentChunkSize(file.getSize());
            fileChunkUploadContext.setTotalSize(totalSize);
            fileChunkUploadContext.setFile(file);
            fileChunkUploadContext.setUserId(userId);

            FileChunkUploadVO fileChunkUploadVO = userFileService.chunkUpload(fileChunkUploadContext);

            if (fileChunkUploadVO.getMergeFlag().equals(MergeFlagEnum.READY.getCode())) {
                System.out.println("分片 " + chunk + " 检测到可以合并分片");

                FileChunkMergeContext fileChunkMergeContext = new FileChunkMergeContext();
                fileChunkMergeContext.setFilename(filename);
                fileChunkMergeContext.setIdentifier(identifier);
                fileChunkMergeContext.setTotalSize(totalSize);
                fileChunkMergeContext.setParentId(parentId);
                fileChunkMergeContext.setUserId(userId);

                userFileService.mergeFile(fileChunkMergeContext);
                countDownLatch.countDown();
            } else {
                countDownLatch.countDown();
            }

        }

    }

    /**
     * 生成模拟的网络文件实体
     *
     * @return
     */
    private static MultipartFile genarateMultipartFile() {
        MultipartFile file = null;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < 1024 * 1024; i++) {
                stringBuffer.append("a");
            }
            file = new MockMultipartFile("file", "test.txt", "multipart/form-data", stringBuffer.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public Long register() {
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