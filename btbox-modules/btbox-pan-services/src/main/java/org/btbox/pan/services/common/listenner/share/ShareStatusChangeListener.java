package org.btbox.pan.services.common.listenner.share;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.pan.services.common.event.file.DeleteFileEvent;
import org.btbox.pan.services.common.event.file.FileRestoreEvent;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: 监听文件状态变更导致分享状态变更的处理器
 * @author: BT-BOX
 * @createDate: 2024/1/25 14:53
 * @version: 1.0
 */
@Component
@RequiredArgsConstructor
public class ShareStatusChangeListener {

    private final UserFileService userFileService;

    private final PanShareService panShareService;

    /**
     * 监听文件被删除之后，刷新所有受影响的分享的状态
     * @param event
     */
    @EventListener(DeleteFileEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void changeShare2FileDeleted(DeleteFileEvent event) {
        List<Long> fileIdList = event.getFileIdList();
        if (CollUtil.isEmpty(fileIdList)) {
            return;
        }
        List<UserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> ObjectUtil.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(UserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        panShareService.refreshShareStatus(allAvailableFileIdList);
    }

    /**
     * 监听文件被还原后，刷新所有受影响分享的状态
     * @param event
     */
    @EventListener(FileRestoreEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void changeShare2Normal(FileRestoreEvent event) {
        List<Long> fileIdList = event.getFileIdList();
        if (CollUtil.isEmpty(fileIdList)) {
            return;
        }
        List<UserFile> allRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
        List<Long> allAvailableFileIdList = allRecords.stream()
                .filter(record -> ObjectUtil.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .map(UserFile::getFileId)
                .collect(Collectors.toList());
        allAvailableFileIdList.addAll(fileIdList);
        panShareService.refreshShareStatus(allAvailableFileIdList);
    }

}