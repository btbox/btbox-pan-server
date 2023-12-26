package org.btbox.pan.services.modules.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.enums.FolderFlagEnum;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.domain.context.QueryFileListContext;
import org.btbox.pan.services.modules.file.domain.context.UpdateFilenameContext;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.repository.mapper.UserFileMapper;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.service.UserFileService;

import java.util.List;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:15
 * @version: 1.0
*/
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService{

    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(
                createFolderContext.getParentId(),
                createFolderContext.getFolderName(),
                FolderFlagEnum.YES,
                null,
                null,
                createFolderContext.getUserId(),
                null
        );
    }

    @Override
    public UserFile getUserRootFile(Long userId) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserFile::getUserId, userId);
        queryWrapper.eq(UserFile::getParentId, FileConstants.TOP_PARENT_ID);
        queryWrapper.eq(UserFile::getFolderFlag, FolderFlagEnum.YES.getCode());
        return this.getOne(queryWrapper);
    }

    @Override
    public List<UserFileVO> getFileList(QueryFileListContext context) {
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(UserFile::getFileId, UserFile::getParentId, UserFile::getFilename, UserFile::getFileSizeDesc, UserFile::getFolderFlag, UserFile::getFileType, UserFile::getUpdateTime);
        queryWrapper.eq(UserFile::getUserId, context.getUserId());
        queryWrapper.eq(null != context.getParentId() && context.getParentId() != -1, UserFile::getParentId, context.getParentId());
        queryWrapper.in(CollUtil.isNotEmpty(context.getFileTypeArray()), UserFile::getFileType, context.getFileTypeArray());
        return MapstructUtils.convert(this.list(queryWrapper), UserFileVO.class);
    }

    /**
     * 更新文件名称
     * 1. 校验更新文件名称操作
     * 2. 执行更新文件名称操作
     * @param context
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    /**
     * 执行更新文件名称操作
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
        long count = this.count(queryWrapper);

        if (count > 0) {
            throw new ServiceException(MessageUtils.message("file.name.used"));
        }

        context.setEntity(entity);
    }


    /**
     * 保存用户文件的映射记录
     * @author: BT-BOX(HJH)
     * @param parentId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param userId
     * @param fileSizeDesc
     * @version: 1.0
     * @createDate: 2023/12/14 15:49
     * @return: java.lang.Long
     */
    private Long saveUserFile(
            Long parentId,
            String filename,
            FolderFlagEnum folderFlagEnum,
            Integer fileType,
            Long realFileId,
            Long userId,
            String fileSizeDesc
    ) {
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
     * @author: BT-BOX(HJH)
     * @param parentId
     * @param userId
     * @param filename
     * @param folderFlagEnum
     * @param fileType
     * @param realFileId
     * @param fileSizeDesc
     * @version: 1.0
     * @createDate: 2023/12/14 15:49
     * @return: org.btbox.pan.services.file.domain.entity.UserFile
     */
    private UserFile assembleUserFile(Long parentId, Long userId, String filename, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, String fileSizeDesc) {
        UserFile entity = new UserFile();
        entity.setFileId(IdWorker.getId());
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
        String newFilenameWithoutSuffix="",newFilenameSuffix;

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
     * @author: BT-BOX(HJH)
     * @param newFilenameWithoutSuffix
     * @param count
     * @param newFilenameSuffix
     * @version: 1.0
     * @createDate: 2023/12/14 16:29
     * @return: java.lang.String
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, long count, String newFilenameSuffix) {
        return newFilenameWithoutSuffix +
                FileConstants.CN_LEFT_PARENTHESES_STR +
                count +
                FileConstants.CN_RIGHT_PARENTHESES_STR +
                newFilenameSuffix;
    }


    /**
     * 查询通义符文件夹下面的同名文件夹数量
     * @author: BT-BOX(HJH)
     * @param entity
     * @param newFilenameWithoutSuffix
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
