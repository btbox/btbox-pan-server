package org.btbox.pan.services.modules.file.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.enums.MergeFlagEnum;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.common.config.PanServerConfig;
import org.btbox.pan.services.modules.file.convert.FileConvert;
import org.btbox.pan.services.modules.file.domain.context.FileChunkSaveContext;
import org.btbox.pan.storage.engine.core.StorageEngine;
import org.btbox.pan.storage.engine.core.context.StoreFileChunkContext;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.file.repository.mapper.PanFileChunkMapper;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import org.btbox.pan.services.modules.file.service.PanFileChunkService;
/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:09
 * @version: 1.0
*/
@Service
@RequiredArgsConstructor
public class PanFileChunkServiceImpl extends ServiceImpl<PanFileChunkMapper, PanFileChunk> implements PanFileChunkService{

    private final PanServerConfig panServerConfig;

    private final FileConvert fileConvert;

    private final StorageEngine storageEngine;

    /**
     * 文件分片保存
     * 1. 保存文件分片和记录
     * 2. 判断文件分片是否全部上传完成
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/28 15:15
     * @return: void
     */
    @Override
    public synchronized void saveChunkFile(FileChunkSaveContext context) {
        doSaveChunkFile(context);
        doJudgeMergeFile(context);
    }

    /**
     * 执行文件分片上传保存的操作
     * 1. 委托文件存储引擎存储文件分片
     * 2. 保存文件分片记录
     * @param context
     */
    private void doSaveChunkFile(FileChunkSaveContext context) {
        doStoreFileChunk(context);
        doSaveRecord(context);
    }

    /**
     * 判断是否所有的分片均没上传完成
     * @param context
     */
    private void doJudgeMergeFile(FileChunkSaveContext context) {
        LambdaQueryWrapper<PanFileChunk> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PanFileChunk::getIdentifier, context.getIdentifier());
        queryWrapper.eq(PanFileChunk::getCreateUser, context.getUserId());
        long count = this.count(queryWrapper);
        if (count == context.getTotalChunks()) {
            context.setMergeFlagEnum(MergeFlagEnum.READY);
        }
    }

    /**
     * 保存分片文件记录
     * @param context
     */
    private void doSaveRecord(FileChunkSaveContext context) {
        PanFileChunk record = new PanFileChunk();
        record.setId(IdUtil.get());
        record.setIdentifier(context.getIdentifier());
        record.setRealPath(context.getRealPath());
        record.setChunkNumber(context.getChunkNumber());
        record.setExpirationTime(DateUtil.offsetDay(new Date(), panServerConfig.getChunkFileExpirationDays()));
        record.setCreateUser(context.getUserId());
        if (!this.save(record)) {
            throw new ServiceException("文件分片上传失败");
        }
    }

    /**
     * 委托文件存储引擎保存文件分片
     * @param context
     */
    private void doStoreFileChunk(FileChunkSaveContext context) {
        try {
            StoreFileChunkContext storeFileChunkContext = fileConvert.fileChunkSaveContext2StoreFileChunkContext(context);
            storeFileChunkContext.setInputStream(context.getFile().getInputStream());
            storageEngine.storeChunk(storeFileChunkContext);
            context.setRealPath(storeFileChunkContext.getRealPath());
        } catch (IOException e) {
            log.error(e.getMessage() ,e);
            throw new ServiceException("文件分片上传失败");
        }
    }


}
