package org.btbox.pan.services.modules.file.service.impl;

import lombok.RequiredArgsConstructor;
import org.assertj.core.util.Lists;
import org.btbox.pan.services.common.event.log.ErrorLogEvent;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.pan.services.modules.file.domain.context.FileSaveContext;
import org.btbox.pan.storage.engine.core.StorageEngine;
import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.btbox.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
