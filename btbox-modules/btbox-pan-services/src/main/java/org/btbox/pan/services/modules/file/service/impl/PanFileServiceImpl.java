package org.btbox.pan.services.modules.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.btbox.pan.services.common.event.log.ErrorLogEvent;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.pan.services.modules.file.domain.context.FileChunkMergeAndSaveContext;
import org.btbox.pan.services.modules.file.domain.context.FileSaveContext;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import org.btbox.pan.services.modules.file.service.PanFileChunkService;
import org.btbox.pan.storage.engine.core.StorageEngine;
import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.btbox.pan.storage.engine.core.context.MergeFileContext;
import org.btbox.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.file.repository.mapper.PanFileMapper;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.btbox.pan.services.modules.file.service.PanFileService;
/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:31
 * @version: 1.0
*/
@Service
@RequiredArgsConstructor
public class PanFileServiceImpl extends ServiceImpl<PanFileMapper, PanFile> implements PanFileService{

    private final StorageEngine storageEngine;

    private final ApplicationContext applicationContext;

    private final PanFileChunkService panFileChunkService;

    /**
     * 上传单文件并保存实体记录
     * 1.上传单文件
     * 2.保存实体记录
     * @param context
     */
    @Override
    public void saveFile(FileSaveContext context) {
        storeMultipartFile(context);
        PanFile record = doSaveFile(
                context.getFilename(),
                context.getRealPath(),
                context.getTotalSize(),
                context.getIdentifier(),
                context.getUserId()
        );
        context.setRecord(record);
    }

    /**
     * 合并物理文件并保存物理文件记录
     * 1. 委托文件引擎合并文件分片
     * 2. 保存物理文件记录
     * @param context
     */
    @Override
    public void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context) {
        doMergeFileChunk(context);
        PanFile record = doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(), context.getIdentifier(), context.getUserId());
        context.setRecord(record);
    }

    /**
     * 委托文件存储引擎合并文件分片
     * 1. 查询文件分片的记录
     * 2. 根据文件分片的记录去合并物理文件
     * 3. 删除文件分片记录
     * 4. 封装合并文件的真实存储路径到上下文信息中
     * @param context
     */
    private void doMergeFileChunk(FileChunkMergeAndSaveContext context) {
        LambdaQueryWrapper<PanFileChunk> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PanFileChunk::getIdentifier, context.getIdentifier());
        queryWrapper.eq(PanFileChunk::getCreateUser, context.getUserId());
        queryWrapper.gt(PanFileChunk::getExpirationTime, new Date());
        List<PanFileChunk> chunkRecoredList = panFileChunkService.list(queryWrapper);
        if (CollUtil.isEmpty(chunkRecoredList)) {
            throw new ServiceException("该文件未找到分片记录");
        }
        List<String> realPathList = chunkRecoredList.stream()
                .sorted(Comparator.comparing(PanFileChunk::getChunkNumber))
                .map(PanFileChunk::getRealPath).collect(Collectors.toList());

        try {
            MergeFileContext mergeFileContext = new MergeFileContext();
            mergeFileContext.setFilename(context.getFilename());
            mergeFileContext.setIdentifier(context.getIdentifier());
            mergeFileContext.setUserId(context.getUserId());
            mergeFileContext.setRealPathList(realPathList);
            storageEngine.mergeFile(mergeFileContext);
            context.setRealPath(mergeFileContext.getRealPath());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new ServiceException("文件分片合并失败");
        }

        List<Long> fileChunkRecordIdList = chunkRecoredList.stream().map(PanFileChunk::getId).collect(Collectors.toList());
        panFileChunkService.removeByIds(fileChunkRecordIdList);

    }


    /**
     * 上传单文件
     * 该方法委托文件存储引擎实现
     * @param context
     */
    private void storeMultipartFile(FileSaveContext context) {
        try {
            StoreFileContext storeFileContext = new StoreFileContext();
            storeFileContext.setInputStream(context.getFile().getInputStream());
            storeFileContext.setFilename(context.getFilename());
            storeFileContext.setTotalSize(context.getTotalSize());
            storageEngine.store(storeFileContext);
            context.setRealPath(storeFileContext.getRealPath());
        } catch (IOException e) {
            throw new ServiceException(MessageUtils.message("file.upload.error"));
        }
    }

    /**
     * 保存文件记录
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     */
    private PanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        PanFile record = aseemblePanFile(filename, realPath, totalSize, identifier, userId);
        if (!this.save(record)) {
            try {
                DeleteFileContext deleteFileContext = new DeleteFileContext();
                deleteFileContext.setRealFilePathList(Lists.newArrayList(realPath));
                storageEngine.delete(deleteFileContext);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                ErrorLogEvent errorLogEvent = new ErrorLogEvent(this, "文件物理删除失败，请执行手动删除！文件路径: {}" + realPath, userId);
                applicationContext.publishEvent(errorLogEvent);
            }
        }
        return record;
    }

    /**
     * 拼装文件实体对象
     * @param filename
     * @param realPath
     * @param totalSize
     * @param identifier
     * @param userId
     * @return
     */
    private PanFile aseemblePanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        PanFile record = new PanFile();
        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtils.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtils.getSuffix(filename));
        record.setCreateUser(userId);
        return record;
    }
}
