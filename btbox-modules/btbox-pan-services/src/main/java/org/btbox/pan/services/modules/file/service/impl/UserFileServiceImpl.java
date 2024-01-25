package org.btbox.pan.services.modules.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.enums.FileTypeEnum;
import org.btbox.common.core.enums.FolderFlagEnum;
import org.btbox.common.core.utils.HttpUtil;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.common.event.file.DeleteFileEvent;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.pan.services.common.event.search.UserSearchEvent;
import org.btbox.pan.services.modules.file.convert.FileConvert;
import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.domain.vo.*;
import org.btbox.pan.services.modules.file.repository.mapper.UserFileMapper;
import org.btbox.pan.services.modules.file.service.PanFileChunkService;
import org.btbox.pan.services.modules.file.service.PanFileService;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.storage.engine.core.StorageEngine;
import org.btbox.pan.storage.engine.core.context.ReadFileContext;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:15
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    private final ApplicationContext applicationContext;

    private final PanFileService panFileService;

    private final FileConvert fileConvert;

    private final PanFileChunkService panFileChunkService;

    private final StorageEngine storageEngine;

    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getParentId(), createFolderContext.getFolderName(), FolderFlagEnum.YES, null, null, createFolderContext.getUserId(), null);
    }

    @Override
    public UserFile getUserRootFile(Long userId) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId, userId);
        queryWrapper.eq(UserFile::getParentId, FileConstants.TOP_PARENT_ID);
        queryWrapper.eq(UserFile::getFolderFlag, FolderFlagEnum.YES.getCode());
        queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        return this.getOne(queryWrapper);
    }

    @Override
    public List<UserFileVO> getFileList(QueryFileListContext context) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserFile::getFileId, UserFile::getParentId, UserFile::getFilename, UserFile::getFileSizeDesc, UserFile::getFolderFlag, UserFile::getFileType, UserFile::getUpdateTime);
        queryWrapper.eq(UserFile::getUserId, context.getUserId());
        queryWrapper.eq(UserFile::getDelFlag, ObjectUtil.isEmpty(context.getDelFlag()) ? DelFlagEnum.NO.getCode() : context.getDelFlag());
        queryWrapper.eq(null != context.getParentId() && context.getParentId() != -1, UserFile::getParentId, context.getParentId());
        queryWrapper.in(CollUtil.isNotEmpty(context.getFileIdList()), UserFile::getFileId, context.getFileIdList());
        queryWrapper.in(CollUtil.isNotEmpty(context.getFileTypeArray()), UserFile::getFileType, context.getFileTypeArray());
        return MapstructUtils.convert(this.list(queryWrapper), UserFileVO.class);
    }

    /**
     * 更新文件名称
     * 1. 校验更新文件名称操作
     * 2. 执行更新文件名称操作
     *
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    /**
     * 批量删除用户文件
     * 1. 校验删除的文件
     * 2. 执行批量删除的动作
     * 3. 发布批量删除文件的事件，给其他模块订阅使用
     *
     * @param context
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        checkFileDeleteCondition(context);
        doDeleteFile(context);
        afterFileDelete(context);
    }

    /**
     * 文件秒传
     * 1. 通过文件的唯一标识，查找对应的文件信息
     * 2. 如果没有查到，直接返回秒传四边
     * 3. 如果查到记录，直接挂载关联关系，返回秒传成功
     *
     * @param context
     * @return
     */
    @Override
    public boolean secUpload(SecUploadFileContext context) {
        PanFile record = getFileByUserIdAndIdentifier(context.getUserId(), context.getIdentifier());

        if (ObjectUtil.isEmpty(record)) {
            return false;
        }
        saveUserFile(
                context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getSuffix(context.getFilename())),
                record.getFileId(),
                context.getUserId(),
                record.getFileSizeDesc()
        );
        return true;
    }

    /**
     * 单文件上传
     * 1. 上传文件并保存实体文件记录
     * 2. 保存用户文件的关系记录
     * @param context
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void upload(FileUploadContext context) {
        saveFile(context);
        saveUserFile(
                context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getSuffix(context.getFilename())),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc()
        );
    }

    /**
     * 文件分片上传
     * 1. 上传实体文件
     * 2. 保存分片文件记录
     * 3. 校验是否全部分片上传完成
     * @param context
     * @return
     */
    @Override
    public FileChunkUploadVO chunkUpload(FileChunkUploadContext context) {
        FileChunkSaveContext fileChunkSaveContext = fileConvert.fileChunkUploadContext2FileChunkSaveContext(context);
        panFileChunkService.saveChunkFile(fileChunkSaveContext);
        FileChunkUploadVO vo = new FileChunkUploadVO();
        vo.setMergeFlag(fileChunkSaveContext.getMergeFlagEnum().getCode());
        return vo;
    }

    /**
     * 查询用户已上传的分片列表
     * 1. 查询以上传的分片列表
     * 2. 封装返回实体
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/28 16:30
     * @return: org.btbox.pan.services.modules.file.domain.vo.UploadedChunksVO
     */
    @Override
    public UploadedChunksVO getUploadedChunks(QueryUploadedChunksContext context) {
        LambdaQueryWrapper<PanFileChunk> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PanFileChunk::getChunkNumber);
        queryWrapper.eq(PanFileChunk::getIdentifier, context.getIdentifier());
        queryWrapper.eq(PanFileChunk::getCreateUser, context.getUserId());
        queryWrapper.gt(PanFileChunk::getExpirationTime, new Date());

        List<Integer> uploadedChunks = panFileChunkService.listObjs(queryWrapper, value -> (Integer) value);

        UploadedChunksVO vo = new UploadedChunksVO();
        vo.setUploadedChunks(uploadedChunks);
        return vo;
    }

    /**
     * 文件分片合并
     * 1. 文件分片物理合并
     * 2. 保存文件实体记录
     * 3. 保存文件用户关系映射
     * @param context
     */
    @Override
    public void mergeFile(FileChunkMergeContext context) {
        mergeFileDChunkAndSaveFile(context);
        saveUserFile(
                context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getSuffix(context.getFilename())),
                context.getRecord().getFileId(),
                context.getUserId(),
                context.getRecord().getFileSizeDesc()
        );
    }

    /**
     * 文件下载
     * 1. 校验文件是否存在，文件是否属于该用户
     * 2. 校验该文件是不是一个文件夹
     * 3. 执行下载动作
     * @param context
     */
    @Override
    public void download(FileDownloadContext context) {
        UserFile record = this.getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (checkIsFolder(record)) {
            throw new ServiceException("文件夹暂不支持下载");
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 文件下载不校验用户是否是上传用户
     * @param context
     */
    @Override
    public void downloadWithoutCheckUser(FileDownloadContext context) {
        UserFile record = this.getById(context.getFileId());
        if (checkIsFolder(record)) {
            throw new ServiceException("文件夹暂不支持下载");
        }
        doDownload(record, context.getResponse());
    }

    /**
     * 文件预览
     * 1. 参数校验：校验文件是否存在，文件是否属于该用户
     * 2. 校验该文件是不是一个文件夹
     * 3. 执行预览的动作
     * @param context
     */
    @Override
    public void preview(FilePreviewContext context) {
        UserFile record = this.getById(context.getFileId());
        checkOperatePermission(record, context.getUserId());
        if (checkIsFolder(record)) {
            throw new ServiceException("文件夹暂不支持预览");
        }
        doPreview(record, context.getResponse());
    }

    /**
     * 查询用户的文件夹树
     * 1. 查询出该用户的所有文件夹列表
     * 2. 在内存中拼装文件夹树
     * @param context
     * @return
     */
    @Override
    public List<FolderTreeNodeVO> getFolderTree(QueryFolderTreeContext context) {
        List<UserFile> folderRecords = queryFolderRecords(context.getUserId());
        List<FolderTreeNodeVO> result = assembleFolderTreeNodeVOList(folderRecords);
        return result;
    }

    /**
     * 文件转移
     * 1. 权限校验
     * 2. 执行转移
     * @param context
     */
    @Override
    public void transfer(TransferFileContext context) {
        checkTransferCondition(context);
        doTransfer(context);
    }

    /**
     * 文件复制
     * 1. 文件校验
     * 2. 执行动作
     * @param context
     */
    @Override
    public void copy(CopyFileContext context) {
        checkCopyCondition(context);
        doCopy(context);
    }

    /**
     * 文件搜索
     * 1. 执行文件搜索
     * 2. 拼装文件的父文件夹名称
     * 3. 执行文件搜索后的后置动作
     * @param context
     * @return
     */
    @Override
    public List<FileSearchResultVO> search(FileSearchContext context) {
        List<FileSearchResultVO> result = doSearch(context);
        fillParentFilename(result);
        afterSearch(context);
        return result;
    }

    /**
     * 获取面包屑列表
     * 1. 获取用户所有文件夹信息
     * 2. 拼接需要用到的面包屑列表
     * @param context
     * @return
     */
    @Override
    public List<BreadcrumbVO> getBreadcrumbs(QueryBreadcrumbsContext context) {
        List<UserFile> folderRecords = queryFolderRecords(context.getUserId());
        Map<Long, BreadcrumbVO> prepareBreadcrumbVOMap = folderRecords.stream().map(BreadcrumbVO::transfer).collect(Collectors.toMap(BreadcrumbVO::getId, v -> v));

        BreadcrumbVO currentNode;
        Long fileId = context.getFileId();

        List<BreadcrumbVO> result = CollUtil.newLinkedList();

        do {
            currentNode = prepareBreadcrumbVOMap.get(fileId);
            if (ObjectUtil.isNotNull(currentNode)) {
                result.add(0, currentNode);
                fileId = currentNode.getParentId();
            }
        } while (ObjectUtil.isNotNull(currentNode));

        return result;
    }

    /**
     * 递归查询所有的子文件信息
     * @return
     */
    @Override
    public List<UserFile> findAllFileRecords(List<UserFile> records) {
        List<UserFile> result = Lists.newArrayList(records);
        if (CollUtil.isEmpty(result)) {
            return result;
        }
        long folderCount = result.stream().filter(record -> ObjectUtil.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).count();
        if (folderCount == 0) {
            return result;
        }
        records.stream().forEach(record -> doFindAllChildRecords(result, record));
        return result;
    }

    /**
     * findAllFileRecordsByFileIdList
     * @param fileIdList
     * @return
     */
    @Override
    public List<UserFile> findAllFileRecordsByFileIdList(List<Long> fileIdList) {
        if (CollUtil.isEmpty(fileIdList)) {
            return Lists.newArrayList();
        }
        List<UserFile> records = this.listByIds(fileIdList);
        if (CollUtil.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return findAllFileRecords(records);
    }

    /**
     * 实体转换
     * @param records
     * @return
     */
    @Override
    public List<UserFileVO> transferVOList(List<UserFile> records) {
        if (CollUtil.isEmpty(records)) {
            return Lists.newArrayList();
        }
        return records.stream().map(fileConvert::userFile2UserFileVO).collect(Collectors.toList());
    }

    /**
     * 递归所有的子文件列表
     * 忽略是否删除的标识
     * @param result
     * @param record
     */
    private void doFindAllChildRecords(List<UserFile> result, UserFile record) {
        if (ObjectUtil.isNull(record)) {
            return;
        }
        if (!checkIsFolder(record)) {
            return;
        }
        List<UserFile> childRecords = findChildRecordsIgnoreDelFlag(record.getFileId());
        if (CollUtil.isEmpty(childRecords)) {
            return;
        }
        result.addAll(childRecords);
        childRecords.stream()
                .filter(childRecord -> ObjectUtil.equals(childRecord.getFolderFlag(), FolderFlagEnum.YES.getCode()))
                .forEach(childRecord -> doFindAllChildRecords(result, childRecord));


    }

    /**
     * 查询文件夹下面的文件记录，忽略删除标识
     * @param fileId
     * @return
     */
    private List<UserFile> findChildRecordsIgnoreDelFlag(Long fileId) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getParentId, fileId);
        List<UserFile> childRecords = this.list(queryWrapper);
        return childRecords;
    }

    /**
     * 搜索的后置操作
     * 1. 发布文件搜索的事件
     * @param context
     */
    private void afterSearch(FileSearchContext context) {
        UserSearchEvent event = new UserSearchEvent(this, context.getKeyword(), context.getUserId());
        applicationContext.publishEvent(event);
    }

    /**
     * 填充文件列表的父文件名称
     * @param result
     */
    private void fillParentFilename(List<FileSearchResultVO> result) {
        if (CollUtil.isEmpty(result)) {
            return;
        }
        List<Long> parentIdList = result.stream().map(FileSearchResultVO::getParentId).collect(Collectors.toList());
        List<UserFile> parentRecords = this.listByIds(parentIdList);
        Map<Long, String> fileId2FilenameMap = parentRecords.stream().collect(Collectors.toMap(UserFile::getFileId, UserFile::getFilename));

        result.forEach(vo -> vo.setParentFilename(fileId2FilenameMap.get(vo.getParentId())));
    }

    /**
     * 搜索文件列表
     * @param context
     * @return
     */
    private List<FileSearchResultVO> doSearch(FileSearchContext context) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserFile::getFileId, UserFile::getParentId, UserFile::getFilename, UserFile::getFileSizeDesc, UserFile::getFolderFlag, UserFile::getFileType, UserFile::getUpdateTime);
        queryWrapper.eq(UserFile::getUserId, context.getUserId());
        queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        queryWrapper.likeRight(UserFile::getFilename, context.getKeyword());
        queryWrapper.in(CollUtil.isNotEmpty(context.getFileTypeArray()), UserFile::getFileType, context.getFileTypeArray());
        return MapstructUtils.convert(this.list(queryWrapper), FileSearchResultVO.class);
    }

    /**
     * 执行文件复制的动作
     * @param context
     */
    private void doCopy(CopyFileContext context) {
        List<UserFile> prepareRecords = context.getPrepareRecords();
        if (CollUtil.isNotEmpty(prepareRecords)) {
            List<UserFile> allRecords = Lists.newArrayList();
            prepareRecords.stream().forEach(record -> assembleCopyChildRecord(allRecords, record, context.getTargetParentId(), context.getUserId()));
            if (!this.saveBatch(allRecords)) {
                throw new ServiceException("文件复制失败");
            }
        }
    }

    /**
     * 拼装 当前文件记录以及所有子文件记录
     * @param allRecords
     * @param record
     * @param targetParentId
     * @param userId
     */
    private void assembleCopyChildRecord(List<UserFile> allRecords, UserFile record, Long targetParentId, Long userId) {
        Long newFileId = IdUtil.get();
        Long oldFileId = record.getFileId();

        record.setParentId(targetParentId);
        record.setFileId(newFileId);
        record.setUserId(userId);
        record.setCreateUser(userId);
        record.setUpdateUser(userId);
        handleDuplicateFilename(record);

        allRecords.add(record);

        if (checkIsFolder(record)) {
            List<UserFile> childRecords = findChildRecords(oldFileId);
            if (CollUtil.isEmpty(childRecords)) {
                return;
            }
            childRecords.stream().forEach(childRecord -> assembleCopyChildRecord(allRecords, childRecord, newFileId, userId));
        }
    }

    /**
     * 查找下一级的
     * @param parentId
     * @return
     */
    private List<UserFile> findChildRecords(Long parentId) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getParentId, parentId);
        queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        return this.list(queryWrapper);
    }

    /**
     * 文件转移条件校验
     *
     * @param context
     */
    private void checkCopyCondition(CopyFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!checkIsFolder(this.getById(targetParentId))) {
            throw new ServiceException("目标文件不是一个文件夹");
        }
        List<Long> fileIdList = context.getFileIdList();
        List<UserFile> prepareRecords = this.listByIds(fileIdList);
        context.setPrepareRecords(prepareRecords);
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new ServiceException("目标文件夹ID不能是选中文件列表的文件夹ID或者子文件夹ID");
        }
    }

    /**
     * 执行文件转移
     * @param context
     */
    private void doTransfer(TransferFileContext context) {
        List<UserFile> prepareRecords = context.getPrepareRecords();
        prepareRecords.forEach(record -> {
            record.setParentId(context.getTargetParentId());
            record.setUserId(context.getUserId());
            record.setCreateUser(context.getUserId());
            record.setUpdateUser(context.getUserId());
            handleDuplicateFilename(record);
        });
        if (!this.updateBatchById(prepareRecords)) {
            throw new ServiceException("文件转移失败");
        }
    }

    /**
     * 文件转移条件校验
     *
     * @param context
     */
    private void checkTransferCondition(TransferFileContext context) {
        Long targetParentId = context.getTargetParentId();
        if (!checkIsFolder(this.getById(targetParentId))) {
            throw new ServiceException("目标文件不是一个文件夹");
        }
        List<Long> fileIdList = context.getFileIdList();
        List<UserFile> prepareRecords = this.listByIds(fileIdList);
        context.setPrepareRecords(prepareRecords);
        if (checkIsChildFolder(prepareRecords, targetParentId, context.getUserId())) {
            throw new ServiceException("目标文件夹ID不能是选中文件列表的文件夹ID或者子文件夹ID");
        }
    }

    /**
     * 校验目标文件夹id是都是要操作的文件夹记录的文件夹ID以及其子文件夹ID
     * 1. 如果要操作的文件列表中没有文件夹，那就直接返回false
     * 2. 拼装文件夹ID以及所有子文件夹ID，判断存即可
     * @param prepareRecords
     * @param targetParentId
     * @param userId
     * @return
     */
    private boolean checkIsChildFolder(List<UserFile> prepareRecords, Long targetParentId, Long userId) {
        prepareRecords = prepareRecords.stream().filter(record -> ObjectUtil.equals(record.getFolderFlag(), FolderFlagEnum.YES.getCode())).collect(Collectors.toList());
        if (CollUtil.isEmpty(prepareRecords)) {
            return false;
        }
        List<UserFile> folderRecords = queryFolderRecords(userId);
        Map<Long, List<UserFile>> folderRecordMap = folderRecords.stream().collect(Collectors.groupingBy(UserFile::getParentId));
        List<UserFile> unavailableFolderRecords = Lists.newArrayList();
        unavailableFolderRecords.addAll(prepareRecords);
        prepareRecords.forEach(record -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, record));
        List<Long> unavailableFolderRecordIds = unavailableFolderRecords.stream().map(UserFile::getFileId).toList();
        return unavailableFolderRecordIds.contains(targetParentId);
    }

    /**
     * 查找文件夹的所有子文件夹记录
     * @param unavailableFolderRecords
     * @param folderRecordMap
     * @param record
     */
    private void findAllChildFolderRecords(List<UserFile> unavailableFolderRecords, Map<Long, List<UserFile>> folderRecordMap, UserFile record) {
        if (ObjectUtil.isEmpty(record)) {
            return;
        }
        List<UserFile> childFolderRecords = folderRecordMap.get(record.getFileId());
        if (CollUtil.isEmpty(childFolderRecords)) {
            return;
        }
        unavailableFolderRecords.addAll(childFolderRecords);
        childFolderRecords.forEach(childRecord -> findAllChildFolderRecords(unavailableFolderRecords, folderRecordMap, childRecord));
    }

    /**
     * 在内存中拼装文件夹树
     * @param folderRecords
     * @return
     */
    private List<FolderTreeNodeVO> assembleFolderTreeNodeVOList(List<UserFile> folderRecords) {
        if (CollUtil.isEmpty(folderRecords)) {
            return Lists.newArrayList();
        }
        List<FolderTreeNodeVO> mappedFolderTreeNodeVOList = folderRecords.stream().map(fileConvert::userFile2FolderTreeNodeVO).toList();
        Map<Long, List<FolderTreeNodeVO>> mappedFolderTreeNodeVOMap = mappedFolderTreeNodeVOList.stream().collect(Collectors.groupingBy(FolderTreeNodeVO::getParentId));

        for (FolderTreeNodeVO node : mappedFolderTreeNodeVOList) {
            List<FolderTreeNodeVO> children = mappedFolderTreeNodeVOMap.get(node.getId());
            if (CollUtil.isNotEmpty(children)) {
                node.getChildren().addAll(children);
            }
        }
        return mappedFolderTreeNodeVOList.stream().filter(node -> ObjectUtil.equals(node.getParentId(), FileConstants.TOP_PARENT_ID)).collect(Collectors.toList());
    }

    /**
     * 查询出该用户的所有文件夹列表
     * @param userId
     * @return
     */
    private List<UserFile> queryFolderRecords(Long userId) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId, userId);
        queryWrapper.eq(UserFile::getFolderFlag, FolderFlagEnum.YES.getCode());
        queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        return this.list(queryWrapper);
    }

    /**
     * 执行文件预览的动作
     * 1. 查询文件的真实存储路径
     * 2. 添加跨域的公共响应头
     * 3. 委托文件存储引擎去读取文件内容到响应的输出流中
     * @param record
     * @param response
     */
    private void doPreview(UserFile record, HttpServletResponse response) {
        PanFile realFileRecord = panFileService.getById(record.getRealFileId());
        if (ObjectUtil.isEmpty(realFileRecord)) {
            throw new ServiceException("当前文件记录不存在");
        }
        addCommonResponseHeader(response, realFileRecord.getFilePreviewContentType());
        realFile2OutStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 执行文件下载的动作
     * 1. 查询文件的真实存储路径
     * 2. 添加跨域的公共响应头
     * 3. 拼装下载文件的名称、长度等等响应信息
     * 4. 委托文件存储引擎去读取文件内容到响应的输出流中
     * @param record
     * @param response
     */
    private void doDownload(UserFile record, HttpServletResponse response) {
        PanFile realFileRecord = panFileService.getById(record.getRealFileId());
        if (ObjectUtil.isEmpty(realFileRecord)) {
            throw new ServiceException("当前文件记录不存在");
        }
        addCommonResponseHeader(response, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        addDownloadAttribute(response, record, realFileRecord);
        realFile2OutStream(realFileRecord.getRealPath(), response);
    }

    /**
     * 委托文件存储引擎去读取文件内容并写入到输出流中
     * @param realPath
     * @param response
     */
    private void realFile2OutStream(String realPath, HttpServletResponse response) {
        try {
            ReadFileContext context = new ReadFileContext();
            context.setRealPath(realPath);
            context.setOutputStream(response.getOutputStream());
            storageEngine.readFile(context);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("文件下载失败");
        }
    }

    /**
     * 添加公共的文件读取响应头
     * @param response
     * @param record
     * @param realFileRecord
     */
    private void addDownloadAttribute(HttpServletResponse response, UserFile record, PanFile realFileRecord) {
        try {
            response.addHeader(
                    FileConstants.CONTENT_DISPOSITION_STR,
                    FileConstants.CONTENT_DISPOSITION_VALUE_PREFIX_STR + new String(record.getFilename().getBytes(FileConstants.GB2312_STR), FileConstants.IOS_8859_1_STR));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("文件下载失败");
        }
        response.setContentLengthLong(Long.parseLong(realFileRecord.getFileSize()));
    }

    /**
     * 添加公共的文件读取响应头
     * @param response
     * @param contentTypeValue
     */
    private void addCommonResponseHeader(HttpServletResponse response, String contentTypeValue) {
        response.reset();
        HttpUtil.addCorsResponseHeaders(response);
        response.addHeader(FileConstants.CONTENT_TYPE_STR, contentTypeValue);
        response.setContentType(contentTypeValue);
    }

    /**
     * 校验当前文件记录是不是一个文件夹
     * @param record
     * @return
     */
    private boolean checkIsFolder(UserFile record) {
        if (ObjectUtil.isEmpty(record)) {
            throw new ServiceException("当前文件记录不存在");
        }
        return FolderFlagEnum.YES.getCode().equals(record.getFolderFlag());
    }

    /**
     * 校验用户操作权限
     * 1. 文件记录必须存在
     * 2. 文件记录的创建必须是当前登录的用户
     * @param record
     * @param userId
     */
    private void checkOperatePermission(UserFile record, Long userId) {
        if (ObjectUtil.isEmpty(record)) {
            throw new ServiceException("当前文件记录不存在");
        }
        if (!record.getUserId().equals(userId)) {
            throw new ServiceException("您没有改文件的操作权限");
        }
    }

    /**
     * 合并文件分片并保存物理文件记录
     * @param context
     */
    private void mergeFileDChunkAndSaveFile(FileChunkMergeContext context) {
        FileChunkMergeAndSaveContext fileChunkMergeAndSaveContext = fileConvert.fileChunkMergeContext2FileChunkMergeAndSaveContext(context);
        panFileService.mergeFileChunkAndSaveFile(fileChunkMergeAndSaveContext);
        context.setRecord(fileChunkMergeAndSaveContext.getRecord());
    }

    /**
     * 上传文件并保存实体记录
     * @param context
     */
    private void saveFile(FileUploadContext context) {
        FileSaveContext fileSaveContext = fileConvert.fileUploadContext2FileSaveContext(context);
        panFileService.saveFile(fileSaveContext);
        context.setRecord(fileSaveContext.getRecord());
    }

    /**
     * 根据用户id和文件唯一标识符查询文件
     * @param userId
     * @param identifier
     * @return
     */
    private PanFile getFileByUserIdAndIdentifier(Long userId, String identifier) {
        LambdaQueryWrapper<PanFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PanFile::getCreateUser, userId);
        queryWrapper.eq(PanFile::getIdentifier, identifier);
        List<PanFile> records = panFileService.list(queryWrapper);
        if (CollUtil.isEmpty(records)) {
            return null;
        }
        return records.get(BtboxConstants.ZERO_INT);
    }

    /**
     * 文件删除的后置操作
     * 1. 对外发布文件删除的事件
     *
     * @param context
     */
    private void afterFileDelete(DeleteFileContext context) {
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, context.getFileIdList());
        applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 执行文件删除操作
     *
     * @param context
     */
    private void doDeleteFile(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        LambdaUpdateWrapper<UserFile> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(UserFile::getFileId, fileIdList);
        updateWrapper.set(UserFile::getDelFlag, DelFlagEnum.YES.getCode());
        if (!this.update(updateWrapper)) {
            throw new ServiceException("文件删除失败");
        }
    }

    /**
     * 删除文件之前的前置校验
     *
     * @param context
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();

        List<UserFile> userFiles = this.listByIds(fileIdList);
        if (userFiles.size() != fileIdList.size()) {
            throw new ServiceException("存在不合法的文件记录");
        }

        Set<Long> fileIdSet = userFiles.stream().map(UserFile::getFileId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();

        if (oldSize != newSize) {
            throw new ServiceException("存在不合法的文件记录");
        }

        // 校验用户是否只有一个
        Set<Long> userIdSet = userFiles.stream().map(UserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new ServiceException("存在不合法的文件记录");
        }

        Long dbUserId = userIdSet.stream().findFirst().get();
        if (!ObjectUtil.equals(dbUserId, context.getUserId())) {
            throw new ServiceException("当前登录用户没有删除该文件的权限");
        }
    }

    /**
     * 执行更新文件名称操作
     *
     * @param context
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        UserFile entity = context.getEntity();
        entity.setFilename(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());

        if (!this.updateById(entity)) {
            throw new ServiceException(MessageUtils.message("file.name.update.error"));
        }
    }

    /**
     * 更新文件名称的条件校验
     * 1. 文件ID是有效的
     * 2. 用户有权限更新改文件的文件名称
     * 3. 新旧文件名称不能一样
     * 4. 不能使用当前文件夹下面的子文件的名称
     *
     * @param context
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {

        Long fileId = context.getFileId();
        String newFilename = context.getNewFilename();
        UserFile entity = this.getById(fileId);

        if (ObjectUtil.isEmpty(entity)) {
            throw new ServiceException(MessageUtils.message("file.info.error"));
        }

        if (!ObjectUtil.equals(entity.getUserId(), context.getUserId())) {
            throw new ServiceException(MessageUtils.message("file.update.filename.not.permission"));
        }

        if (ObjectUtil.equals(entity.getFilename(), newFilename)) {
            throw new ServiceException(MessageUtils.message("file.name.same"));
        }

        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getParentId, entity.getParentId());
        queryWrapper.eq(UserFile::getFilename, newFilename);
        queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        long count = this.count(queryWrapper);

        if (count > 0) {
            throw new ServiceException(MessageUtils.message("file.name.used"));
        }

        context.setEntity(entity);
    }


    /**
     * 保存用户文件的映射记录
     *
     * @param parentId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/14 15:49
     * @return: java.lang.Long
     */
    private Long saveUserFile(Long parentId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        UserFile entity = assembleUserFile(parentId, userId, filename, folderFlagEnum, fileType, realFileId, fileSizeDesc);
        if (!this.save(entity)) {
            throw new ServiceException(MessageUtils.message("file.save.error"));
        }
        return entity.getFileId();
    }

    /**
     * 用户文件映射关系实体转化
     * 1、构建并填充实体
     * 2、处理文件命名一致的问题
     *
     * @param parentId
     * @param userId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param fileSizeDesc
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/14 15:49
     * @return: org.btbox.pan.services.file.domain.entity.UserFile
     */
    private UserFile assembleUserFile(Long parentId, Long userId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, String fileSizeDesc) {
        UserFile entity = new UserFile();
        entity.setFileId(IdUtil.get());
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFilename(filename);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileType(fileType);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);

        return entity;
    }

    private void handleDuplicateFilename(UserFile entity) {
        String filename = entity.getFilename();
        String newFilenameWithoutSuffix = "", newFilenameSuffix;

        int newFilenamePointPosition = filename.lastIndexOf(BtboxConstants.POINT_STR);
        if (newFilenamePointPosition == BtboxConstants.MINUS_ONE_INT) {
            newFilenameSuffix = filename;
            newFilenameSuffix = StrUtil.EMPTY;
        } else {
            newFilenameWithoutSuffix = filename.substring(BtboxConstants.ZERO_INT, newFilenamePointPosition);
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, StrUtil.EMPTY);
        }

        long count = getDuplicateFilename(entity, newFilenameWithoutSuffix);
        if (count == 0) {
            return;
        }
        String newFilename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);
        entity.setFilename(newFilename);
    }

    /**
     * 碰撞新文件名称
     * 拼装规则参考操作系统重复文件名称的重命名规范
     *
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/14 16:29
     * @return: java.lang.String
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, long count, String newFilenameSuffix) {
        return newFilenameWithoutSuffix + FileConstants.CN_LEFT_PARENTHESES_STR + count + FileConstants.CN_RIGHT_PARENTHESES_STR + newFilenameSuffix;
    }


    /**
     * 查询通义符文件夹下面的同名文件夹数量
     *
     * @param entity
     * @param newFilenameWithoutSuffix
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/14 16:22
     * @return: int
     */
    private long getDuplicateFilename(UserFile entity, String newFilenameWithoutSuffix) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getParentId, entity.getParentId());
        queryWrapper.eq(UserFile::getFolderFlag, entity.getFolderFlag());
        queryWrapper.eq(UserFile::getUserId, entity.getFolderFlag());
        queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
        queryWrapper.like(UserFile::getFilename, newFilenameWithoutSuffix);
        return this.count(queryWrapper);
    }

}
