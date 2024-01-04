package org.btbox.pan.services.common.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.pan.services.common.event.log.ErrorLogEvent;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import org.btbox.pan.services.modules.file.service.PanFileChunkService;
import org.btbox.pan.storage.engine.core.StorageEngine;
import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/4 15:29
 * @version: 1.0
*/
@Component
@RequiredArgsConstructor
@Slf4j
public class CleanExpireChunkFileTask {

    private static final Long BATCH_SIZE = 500L;

    private final PanFileChunkService panFileChunkService;

    private final StorageEngine storageEngine;

    private final ApplicationContext applicationContext;

    @Scheduled(cron = "1 0 0 * * ? ")
    public void clearExpireChunkFile() {
        log.info("{} 任务开始清空过期的分片文件...", this.getClass().getName());

        List<PanFileChunk> expireFileChunkRecords;
        Long scrollPointer = 1L;

        do {
            expireFileChunkRecords = scrollQueryExpireFileChunkRecords(scrollPointer);
            if (CollUtil.isNotEmpty(expireFileChunkRecords)) {
                // 删除分片文件真实物理文件
                deleteRealChunkFiles(expireFileChunkRecords);
                // 删除数据库中的分片文件记录
                List<Long> idList = deleteChunkFileRecords(expireFileChunkRecords);
                scrollPointer = Collections.max(idList);
            }
        } while (CollUtil.isNotEmpty(expireFileChunkRecords));

        log.info("{} 任务结束清空过期的分片文件...", this.getClass().getName());
    }




    /***************************************** private **************************************/

    /**
     * 滚动查询过期的分片文件记录
     * @param scrollPointer
     * @return
     */
    private List<PanFileChunk> scrollQueryExpireFileChunkRecords(Long scrollPointer) {
        LambdaQueryWrapper<PanFileChunk> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(PanFileChunk::getExpirationTime, new Date());
        queryWrapper.ge(PanFileChunk::getId, scrollPointer);
        queryWrapper.last(" limit " + BATCH_SIZE);
        return panFileChunkService.list(queryWrapper);
    }

    /**
     * 物理删除过期的分片文件
     * @param expireFileChunkRecords
     */
    private void deleteRealChunkFiles(List<PanFileChunk> expireFileChunkRecords) {

        DeleteFileContext deleteFileContext = new DeleteFileContext();

        List<String> realPaths = expireFileChunkRecords.stream().map(PanFileChunk::getRealPath).collect(Collectors.toList());
        deleteFileContext.setRealFilePathList(realPaths);
        try {
            storageEngine.delete(deleteFileContext);
        } catch (IOException e) {
            saveErrorLog(realPaths);
        }

    }


    /**
     * 发布物理文件删除失败事件
     * @param realPaths
     */
    private void saveErrorLog(List<String> realPaths) {
        ErrorLogEvent event = new ErrorLogEvent(this, "文件物理删除失败，请手动执行文件删除！文件路径为：" + JSONUtil.toJsonStr(realPaths), BtboxConstants.ZERO_LONG);
        applicationContext.publishEvent(event);
    }

    /**
     * 删除过期文件分片记录
     * @param expireFileChunkRecords
     * @return
     */
    private List<Long> deleteChunkFileRecords(List<PanFileChunk> expireFileChunkRecords) {
        List<Long> idList = expireFileChunkRecords.stream().map(PanFileChunk::getId).collect(Collectors.toList());
        panFileChunkService.removeByIds(idList);
        return idList;
    }

}