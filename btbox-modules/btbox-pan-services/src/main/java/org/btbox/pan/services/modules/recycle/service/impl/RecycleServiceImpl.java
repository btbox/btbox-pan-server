package org.btbox.pan.services.modules.recycle.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.pan.services.common.event.file.FileRestoreEvent;
import org.btbox.pan.services.modules.file.domain.context.QueryFileListContext;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.recycle.domain.context.QueryRecycleFileListContext;
import org.btbox.pan.services.modules.recycle.domain.context.RestoreContext;
import org.btbox.pan.services.modules.recycle.service.RecycleService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/4 16:11
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class RecycleServiceImpl implements RecycleService {

    private final UserFileService userFileService;

    private final ApplicationContext applicationContext;

    /**
     * 查询用户的回收站文件列表
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> recycles(QueryRecycleFileListContext context) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        return userFileService.getFileList(queryFileListContext);
    }

    /**
     * 文件还原
     * 1. 检查操作权限
     * 2. 检查是否可以还原
     * 3. 执行文件还原你的操作
     * 4. 执行文件还原的后置操作
     * @param context
     */
    @Override
    public void restore(RestoreContext context) {
        checkRestorePermission(context);
        checkRestoreFilename(context);
        doRestore(context);
        afterRestore(context);
    }

    /********************************** private ******************************/

    /**
     * 检查文件还原的操作权限
     * @param context
     */
    private void checkRestorePermission(RestoreContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<UserFile> records = userFileService.listByIds(fileIdList);

        if (CollUtil.isEmpty(records)) {
            throw new ServiceException("文件还原失败");
        }
        Set<Long> userIdSet = records.stream().map(UserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() > 1) {
            throw new ServiceException("您无权执行文件还原");
        }

        if (!userIdSet.contains(context.getUserId())) {
            throw new ServiceException("您无权执行文件还原");
        }

        context.setRecords(records);
    }

    /**
     * 检查要还原的文件名称是否被占用
     * @param context
     */
    private void checkRestoreFilename(RestoreContext context) {

        List<UserFile> records = context.getRecords();
        Set<String> filenameSet = records.stream().map(record -> record.getFilename() + BtboxConstants.COMMON_SEPARATOR + record.getParentId()).collect(Collectors.toSet());

        if (filenameSet.size() != records.size()) {
            throw new ServiceException("文件还原失败，该还原文件中存在同名文件，请逐个还原并重命名");
        }

        for (UserFile record : records) {
            LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserFile::getUserId, context.getUserId());
            queryWrapper.eq(UserFile::getParentId, record.getParentId());
            queryWrapper.eq(UserFile::getFilename, record.getFilename());
            queryWrapper.eq(UserFile::getDelFlag, DelFlagEnum.NO.getCode());
            if (userFileService.count(queryWrapper) > 0) {
                throw new ServiceException("文件：" + record.getFilename() + " 还原失败，该文件夹下面已经存在了相同的名称的文件或者文件夹，请重命名后再执行文件还原");
            }
        }
    }

    /**
     * 文件还原的后置操作
     * 1. 发布文件还原事件
     * @param context
     */
    private void afterRestore(RestoreContext context) {
        FileRestoreEvent event = new FileRestoreEvent(this, context.getFileIdList());
        applicationContext.publishEvent(event);
    }

    /**
     * 执行文件还原的动作
     */
    private void doRestore(RestoreContext context) {
        List<UserFile> records = context.getRecords();
        records.forEach(record -> {
            record.setDelFlag(DelFlagEnum.NO.getCode());
        });
        boolean updateFlag = userFileService.updateBatchById(records);
        if (!updateFlag) {
            throw new ServiceException("文件还原失败");
        }
    }
}