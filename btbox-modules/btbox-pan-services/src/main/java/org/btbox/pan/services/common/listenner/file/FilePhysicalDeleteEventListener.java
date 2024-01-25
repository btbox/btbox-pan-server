package org.btbox.pan.services.common.listenner.file;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.enums.FolderFlagEnum;
import org.btbox.pan.services.common.event.file.FilePhysicalDeleteEvent;
import org.btbox.pan.services.common.event.log.ErrorLogEvent;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.service.PanFileService;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.storage.engine.core.StorageEngine;
import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 文件物理删除监听器
 * @author: BT-BOX
 * @createDate: 2024/1/10 17:03
 * @version: 1.0
 */
@Component
@RequiredArgsConstructor
public class FilePhysicalDeleteEventListener {

    private final PanFileService panFileService;

    private final UserFileService userFileService;

    private final StorageEngine storageEngine;

    private final ApplicationContext applicationContext;

    /**
     * 监听文件物理删除事件执行器
     * 该执行器是一个资源释放器，释放被物理删除的文件列表中关联的实体文件记录
     * 1. 查询所有无引用的实体文件记录
     * 2. 删除记录
     * 3. 物理清理文件(委托文件存储引擎)
     */
    @EventListener(classes = FilePhysicalDeleteEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void physicalDeleteFile(FilePhysicalDeleteEvent event) {
        List<UserFile> allRecords = event.getAllRecords();
        if (CollUtil.isEmpty(allRecords)) {
            return;
        }
        List<Long> realFileIdList = findAllUnusedRealFileIdList(allRecords);
        List<PanFile> realFileRecords = panFileService.listByIds(realFileIdList);
        if (CollUtil.isEmpty(realFileRecords)) {
            return;
        }
        if (!userFileService.removeByIds(realFileIdList)) {
            applicationContext.publishEvent(new ErrorLogEvent(this, "实体文件记录: " + realFileIdList + ", 物理删除失败, 请执行手动删除", BtboxConstants.ZERO_LONG));
            return;
        }
        physicalDeleteFileByStorageEngine(realFileRecords);
    }

    /**
     * 委托文件存储引擎执行物理文件的删除
     * @param realFileRecords
     */
    private void physicalDeleteFileByStorageEngine(List<PanFile> realFileRecords) {
        List<String> realPathList = realFileRecords.stream().map(PanFile::getRealPath).toList();
        DeleteFileContext deleteFileContext = new DeleteFileContext();
        deleteFileContext.setRealFilePathList(realPathList);
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            applicationContext.publishEvent(new ErrorLogEvent(this, "实体文件: " + JSONUtil.toJsonStr(realPathList) + ", 物理删除失败, 请执行手动删除", BtboxConstants.ZERO_LONG));
        }
    }

    /**
     * 查询所有没有被引用的真实文件记录ID集合
     * @param allRecords
     * @return
     */
    private List<Long> findAllUnusedRealFileIdList(List<UserFile> allRecords) {
        List<Long> realFileIdList = allRecords.stream()
                .filter(record -> ObjectUtil.equals(record.getFolderFlag(), FolderFlagEnum.NO.getCode()))
                .filter(this::isUnused)
                .map(UserFile::getRealFileId)
                .collect(Collectors.toList());
        return realFileIdList;
    }

    /**
     * 校验文件的真实文件ID是不是没有被引用了
     * @param record
     * @return
     */
    private boolean isUnused(UserFile record) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getRealFileId, record.getRealFileId());
        return userFileService.count(queryWrapper) == BtboxConstants.ZERO_INT;
    }

}