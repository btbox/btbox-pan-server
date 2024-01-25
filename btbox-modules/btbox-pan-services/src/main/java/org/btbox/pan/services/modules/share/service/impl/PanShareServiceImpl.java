package org.btbox.pan.services.modules.share.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.enums.ResponseCode;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.core.utils.JwtUtil;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.pan.services.common.config.PanServerConfig;
import org.btbox.pan.services.common.event.log.ErrorLogEvent;
import org.btbox.pan.services.modules.file.domain.context.CopyFileContext;
import org.btbox.pan.services.modules.file.domain.context.FileDownloadContext;
import org.btbox.pan.services.modules.file.domain.context.QueryFileListContext;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.share.constants.ShareConstants;
import org.btbox.pan.services.modules.share.convert.CancelShareContext;
import org.btbox.pan.services.modules.share.convert.ShareSaveContext;
import org.btbox.pan.services.modules.share.domain.context.*;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.btbox.pan.services.modules.share.domain.entity.PanShareFile;
import org.btbox.pan.services.modules.share.domain.vo.*;
import org.btbox.pan.services.modules.share.enums.ShareDayTypeEnum;
import org.btbox.pan.services.modules.share.enums.ShareStatusEnum;
import org.btbox.pan.services.modules.share.repository.mapper.PanShareMapper;
import org.btbox.pan.services.modules.share.service.PanShareFileService;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/16 10:38
 * @version: 1.0
*/
@Service
@RequiredArgsConstructor
public class PanShareServiceImpl extends ServiceImpl<PanShareMapper, PanShare> implements PanShareService{

    private final PanServerConfig panServerConfig;

    private final PanShareFileService panShareFileService;

    private final UserFileService userFileService;

    private final UserService userService;

    private final ApplicationContext applicationContext;

    /**
     * 创建分享链接
     * 1. 拼装分享实体，保存到数据库
     * 2. 保存分享和对应文件的关联关系
     * 3. 拼装返回实体并返回
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/16 11:23
     * @return: org.btbox.pan.services.modules.share.domain.vo.RPanShareUrlVO
     */
    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public PanShareUrlVO create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        return assembleShareVO(context);
    }

    /**
     * 查询用户的分享列表
     *
     * @param context
     * @return
     */
    @Override
    public List<PanShareUrlListVO> getShares(QueryShareListContext context) {
        LambdaQueryWrapper<PanShare> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(
                PanShare::getShareId,
                PanShare::getShareName,
                PanShare::getShareType,
                PanShare::getShareDayType,
                PanShare::getShareEndTime,
                PanShare::getShareUrl,
                PanShare::getShareCode,
                PanShare::getShareStatus,
                PanShare::getCreateTime
        );
        queryWrapper.eq(PanShare::getCreateUser, context.getUserId());
        return MapstructUtils.convert(this.list(queryWrapper), PanShareUrlListVO.class);
    }

    /**
     * 取消分享链接
     * 1. 校验用户的操作权限
     * 2. 删除对应的分享记录
     * 3. 删除对应的分享文件关联关系记录
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/16 16:11
     * @return: void
     */
    @Transactional(rollbackFor = ServiceException.class)
    @Override
    public void cancelShare(CancelShareContext context) {
        checkUserCancelSharePermission(context);
        doCancelShare(context);
        doCancelShareFiles(context);
    }

    /**
     * 校验分享码
     * 1. 检查分享的状态是否正常
     * 2. 校验分享的分享码是否正确
     * 3. 生成一个短时间的分享token返回给上游
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/16 16:43
     * @return: java.lang.String
     */
    @Override
    public String checkShareCode(CheckShareCodeContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        doCheckShareCode(context);
        return generateShareToken(context);
    }

    /**
     * 查询分享详情信息
     * 1. 校验分享的状态
     * 2. 初始化分享实体
     * 3. 查询分享的主体信息
     * 4. 查询分享的文件列表
     * 5. 查询分享者的信息
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/17 9:42
     * @return: org.btbox.pan.services.modules.share.domain.vo.ShareDetailVO
     */
    @Override
    public ShareDetailVO detail(QueryShareDetailContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareVO(context);
        assembleMainShareInfo(context);
        assembleShareFilesInfo(context);
        assembleShareUserInfo(context);
        return context.getVo();
    }

    /**
     * 查询分享的简单详情
     * 1. 校验分享的状态
     * 2. 初始化分享实体
     * 3. 查询分享的主体信息
     * 4. 查询分享者的信息
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/17 11:21
     * @return: org.btbox.pan.services.modules.share.domain.vo.ShareSimpleDetailVO
     */
    @Override
    public ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        initShareSimpleVO(context);
        assembleMainSimpleShareInfo(context);
        assembleShareSimpleUserInfo(context);
        return context.getVo();
    }

    /**
     * 获取下一级的文件列表
     * 1. 校验分享的状态
     * 2. 校验文件的ID在分享的文件列表中
     * 3. 查询对应文件的子文件列表返回
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2024/1/25 10:29
     * @return: java.util.List<org.btbox.pan.services.modules.file.domain.vo.UserFileVO>
     */
    @Override
    public List<UserFileVO> fileList(QueryChildFileListContext context) {
        PanShare record = checkShareStatus(context.getShareId());
        context.setRecord(record);
        List<UserFileVO> allUserFileRecords = checkFileIdIsOnShareStatusAndGetAllShareUserFiles(context.getShareId(), Lists.newArrayList(context.getParentId()));
        Map<Long, List<UserFileVO>> parentIdFileListMap = allUserFileRecords.stream().collect(Collectors.groupingBy(UserFileVO::getParentId));
        List<UserFileVO> userFileVOS = parentIdFileListMap.get(context.getParentId());
        if (CollUtil.isEmpty(userFileVOS)) {
            return Lists.newArrayList();
        }
        return userFileVOS;
    }

    /**
     * 转存至我的网盘
     * 1. 校验分享状态
     * 2. 校验文件ID是否合法
     * 3. 委托文件模块做文件拷贝的操作
     * @param context
     */
    @Override
    public void saveFiles(ShareSaveContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), context.getFileIdList());
        doSaveFiles(context);
    }

    /**
     * 分享文件的下载
     * 1. 校验分享状态
     * 2. 校验文件ID的合法性
     * 3. 执行文件下载的动作
     * @param context
     */
    @Override
    public void download(ShareFileDownloadContext context) {
        checkShareStatus(context.getShareId());
        checkFileIdIsOnShareStatus(context.getShareId(), Lists.newArrayList(context.getFileId()));
        doDownload(context);
    }

    /**
     * 刷新受影响的对应分享的状态
     * 1. 查询所有受影响的分享的ID集合
     * 2. 去判断每一个分享对应的文件以及所有父文件信息均为正常，这种情况，把分享的状态变为正常
     * 3. 如果有分享的文件或者父文件信息被删除，变更该分享的状态为有文件被删除
     * @param allAvailableFileIdList
     */
    @Override
    public void refreshShareStatus(List<Long> allAvailableFileIdList) {
        List<Long> shareIdList = getShareIdListByFileIdList(allAvailableFileIdList);
        if (CollUtil.isEmpty(shareIdList)) {
            return;
        }
        Set<Long> shareIdSet = Sets.newHashSet(shareIdList);
        shareIdSet.stream().forEach(this::refreshOneShareStatus);
    }

    /***************************************** private ****************************************/


    /**
     * 刷新一个分享的分享状态
     * 1. 查询对应的分享信息，判断有效
     * 2. 去判断该分享对应的文件以及所有父文件信息均为正常，该种情况，把分享的状态变为正常
     * 3. 如果有分享的文件或者父文件信息被删除，变更该分享的状态为有文件被删除
     * @param shareId
     */
    private void refreshOneShareStatus(Long shareId) {
        PanShare record = this.getById(shareId);
        if (ObjectUtil.isNull(record)) {
            return;
        }
        ShareStatusEnum shareStatus = ShareStatusEnum.NORMAL;
        if (!checkShareFileAvailable(shareId)) {
            shareStatus = ShareStatusEnum.FILE_DELETED;
        }
        if (ObjectUtil.equals(record.getShareStatus(), shareStatus.getCode())) {
            return;
        }
        doChangeShareStatus(shareId, shareStatus);
    }

    /**
     * 执行刷新文件分享的状态的动作
     * @param shareId
     * @param shareStatus
     */
    private void doChangeShareStatus(Long shareId, ShareStatusEnum shareStatus) {
        LambdaUpdateWrapper<PanShare> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PanShare::getShareId, shareId);
        updateWrapper.set(PanShare::getShareStatus, shareStatus.getCode());
        if (!this.update(updateWrapper)) {
            applicationContext.publishEvent(new ErrorLogEvent(this, "更新分享状态失败,请手动更改状态,分享ID为:" + shareId + ",分享状态改为:" + shareStatus.getCode(), BtboxConstants.ZERO_LONG));
        }
    }

    /**
     * 检查该分享所有的文件以及所有的父文件均为正常状态
     * @param shareId
     * @return
     */
    private boolean checkShareFileAvailable(Long shareId) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        for (Long fileId : shareFileIdList) {
            if (!checkUpFileAvailable(fileId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查该文件以及所有的文件夹信息均为正常状态
     * @param fileId
     * @return
     */
    private boolean checkUpFileAvailable(Long fileId) {
        UserFile record = userFileService.getById(fileId);
        if (ObjectUtil.isNull(record)) {
            return false;
        }
        if (ObjectUtil.equals(record.getDelFlag(), DelFlagEnum.YES.getCode())) {
            return false;
        }
        if (ObjectUtil.equals(record.getParentId(), FileConstants.TOP_PARENT_ID)) {
            return true;
        }
        return checkUpFileAvailable(record.getParentId());
    }

    /**
     * 通过文件ID查询对应的分享ID集合
     * @param allAvailableFileIdList
     * @return
     */
    private List<Long> getShareIdListByFileIdList(List<Long> allAvailableFileIdList) {
        LambdaQueryWrapper<PanShareFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PanShareFile::getShareId);
        queryWrapper.in(PanShareFile::getFileId, allAvailableFileIdList);
        List<Long> shareIdList = panShareFileService.listObjs(queryWrapper, value -> (Long) value);
        return shareIdList;
    }

    /**
     * 执行分享文件下载
     * @param context
     */
    private void doDownload(ShareFileDownloadContext context) {
        FileDownloadContext fileDownloadContext = new FileDownloadContext();
        fileDownloadContext.setFileId(context.getFileId());
        fileDownloadContext.setUserId(context.getUserId());
        fileDownloadContext.setResponse(context.getResponse());
        userFileService.downloadWithoutCheckUser(fileDownloadContext);
    }

    /**
     * 执行保存我的网盘操作
     * 委托文件模块做文件拷贝的操作
     * @param context
     */
    private void doSaveFiles(ShareSaveContext context) {
        CopyFileContext copyFileContext = new CopyFileContext();
        copyFileContext.setFileIdList(context.getFileIdList());
        copyFileContext.setTargetParentId(context.getTargetParentId());
        copyFileContext.setUserId(context.getUserId());
        userFileService.copy(copyFileContext);
    }

    /**
     * 校验文件ID是否属于一个分享
     * @param shareId
     * @param fileIdList
     */
    private void checkFileIdIsOnShareStatus(Long shareId, List<Long> fileIdList) {
        checkFileIdIsOnShareStatusAndGetAllShareUserFiles(shareId, fileIdList);
    }

    /**
     * 校验文件是否处于分享状态
     * @param shareId
     * @param fileIdList
     * @return
     */
    private List<UserFileVO> checkFileIdIsOnShareStatusAndGetAllShareUserFiles(Long shareId, List<Long> fileIdList) {
        List<Long> shareFileIdList = getShareFileIdList(shareId);
        if (CollUtil.isEmpty(shareFileIdList)) {
            return Lists.newArrayList();
        }
        List<UserFile> allFileRecords = userFileService.findAllFileRecordsByFileIdList(fileIdList);
        if (CollUtil.isEmpty(allFileRecords)) {
            return Lists.newArrayList();
        }
        allFileRecords = allFileRecords.stream()
                .filter(Objects::nonNull)
                .filter(record -> ObjectUtil.equals(record.getDelFlag(), DelFlagEnum.NO.getCode()))
                .collect(Collectors.toList());

        List<Long> allFileIdList = allFileRecords.stream().map(UserFile::getFileId).collect(Collectors.toList());

        if (new HashSet<>(allFileIdList).containsAll(fileIdList)) {
            return userFileService.transferVOList(allFileRecords);
        }

        throw new ServiceException(ResponseCode.SHARE_FILE_MISS.getDesc(), ResponseCode.SHARE_FILE_MISS.getCode());
    }

    /**
     * 查询分享对应的文件ID集合
     * @param shareId
     * @return
     */
    private List<Long> getShareFileIdList(Long shareId) {
        if (ObjectUtil.isNull(shareId)) {
            return Lists.newArrayList();
        }
        LambdaQueryWrapper<PanShareFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PanShareFile::getFileId);
        queryWrapper.eq(PanShareFile::getShareId, shareId);
        List<Long> fileIdList = panShareFileService.listObjs(queryWrapper, value -> (Long) value);
        return fileIdList;
    }

    /**
     * 拼装简单分享详情的用户信息
     * @param context
     */
    private void assembleShareSimpleUserInfo(QueryShareSimpleDetailContext context) {
        BtboxPanUser record = userService.getById(context.getRecord().getCreateUser());
        if (ObjectUtil.isNull(record)) {
            throw new ServiceException("用户信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(DesensitizedUtil.chineseName(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 填充简单分享详情实体信息
     * @param context
     */
    private void assembleMainSimpleShareInfo(QueryShareSimpleDetailContext context) {
        PanShare record = context.getRecord();
        ShareSimpleDetailVO vo = context.getVo();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
    }

    /**
     * 初始化简单分享详情的VO对象
     * @param context
     */
    private void initShareSimpleVO(QueryShareSimpleDetailContext context) {
        ShareSimpleDetailVO vo = new ShareSimpleDetailVO();
        context.setVo(vo);
    }

    /**
     * 查询分享者的信息
     * @param context
     */
    private void assembleShareUserInfo(QueryShareDetailContext context) {
        BtboxPanUser record = userService.getById(context.getRecord().getCreateUser());
        if (ObjectUtil.isNull(record)) {
            throw new ServiceException("用户信息查询失败");
        }
        ShareUserInfoVO shareUserInfoVO = new ShareUserInfoVO();
        shareUserInfoVO.setUserId(record.getUserId());
        shareUserInfoVO.setUsername(DesensitizedUtil.chineseName(record.getUsername()));

        context.getVo().setShareUserInfoVO(shareUserInfoVO);
    }

    /**
     * 查询分享对应的文件列表
     * 1. 查询分享对应的文件ID集合
     * 2. 根据文件ID来查询文件列表信息
     * @param context
     */
    private void assembleShareFilesInfo(QueryShareDetailContext context) {
        List<Long> fileIdList = getShareFileIdList(context.getShareId());

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getRecord().getCreateUser());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());
        queryFileListContext.setFileIdList(fileIdList);

        List<UserFileVO> userFileVOList = userFileService.getFileList(queryFileListContext);
        context.getVo().setUserFileVOList(userFileVOList);
    }

    /**
     * 查询分享的主体信息
     * @param context
     */
    private void assembleMainShareInfo(QueryShareDetailContext context) {
        PanShare record = context.getRecord();
        ShareDetailVO vo = context.getVo();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setCreateTime(record.getCreateTime());
        vo.setShareDay(record.getShareDay());
        vo.setShareEndTime(record.getShareEndTime());
    }

    /**
     * 初始化文件详情的VO实体
     * @param context
     */
    private void initShareVO(QueryShareDetailContext context) {
        ShareDetailVO vo = new ShareDetailVO();
        context.setVo(vo);
    }

    /**
     * 生成短期的token
     * @param context
     * @return
     */
    private String generateShareToken(CheckShareCodeContext context) {
        PanShare record = context.getRecord();
        return JwtUtil.generateToken(cn.hutool.core.util.IdUtil.fastSimpleUUID(), ShareConstants.SHARE_ID, record.getShareId(), ShareConstants.ONE_HOUR_LONG);
    }

    /**
     * 校验分享码是不是正确
     * @param context
     */
    private void doCheckShareCode(CheckShareCodeContext context) {
        PanShare record = context.getRecord();
        if (!ObjectUtil.equals(context.getShareCode(), record.getShareCode())) {
            throw new ServiceException("分享码错误");
        }
    }

    /**
     * 检查分享的状态是否正常
     * @param shareId
     * @return
     */
    private PanShare checkShareStatus(Long shareId) {
        PanShare record = this.getById(shareId);
        if (ObjectUtil.isNull(record)) {
            throw new ServiceException(ResponseCode.SHARE_CANCELLED.getDesc(), ResponseCode.SHARE_CANCELLED.getCode());
        }

        if (ObjectUtil.equals(ShareStatusEnum.FILE_DELETED.getCode(), record.getShareStatus())) {
            throw new ServiceException(ResponseCode.SHARE_FILE_MISS.getDesc(), ResponseCode.SHARE_FILE_MISS.getCode());
        }

        if (ObjectUtil.equals(ShareDayTypeEnum.PERMANENT_VALIDITY.getCode(), record.getShareStatus())) {
            return record;
        }

        if (record.getShareEndTime().before(new Date())) {
            throw new ServiceException(ResponseCode.SHARE_EXPIRE.getDesc(), ResponseCode.SHARE_EXPIRE.getCode());
        }
        return record;
    }

    /**
     * 取消文件和分享的关联关系数据
     * @param context
     */
    private void doCancelShareFiles(CancelShareContext context) {
        LambdaQueryWrapper<PanShareFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(PanShareFile::getShareId, context.getShareIdList());
        queryWrapper.eq(PanShareFile::getCreateUser, context.getUserId());
        if (!panShareFileService.remove(queryWrapper)) {
            throw new ServiceException("取消分享失败");
        }
    }

    /**
     * 执行取消文件分享的动作
     * @param context
     */
    private void doCancelShare(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        if (!this.removeByIds(shareIdList)) {
            throw new ServiceException("取消分享失败");
        }
    }

    /**
     * 检查用户是否拥有取消对应分享链接的权限
     * @param context
     */
    private void checkUserCancelSharePermission(CancelShareContext context) {
        List<Long> shareIdList = context.getShareIdList();
        Long userId = context.getUserId();
        List<PanShare> records = this.listByIds(shareIdList);
        if (CollUtil.isEmpty(records)) {
            throw new ServiceException("您无权限操作取消分享的动作");
        }
        for (PanShare record : records) {
            if (!ObjectUtil.equals(userId, record.getCreateUser())) {
                throw new ServiceException("您无权限操作取消分享的动作");
            }
        }
    }

    /**
     * 拼装对应的返回VO
     * @param context
     * @return
     */
    private PanShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        PanShare record = context.getRecord();
        PanShareUrlVO vo = new PanShareUrlVO();
        vo.setShareId(record.getShareId());
        vo.setShareName(record.getShareName());
        vo.setShareUrl(record.getShareUrl());
        vo.setShareCode(record.getShareCode());
        vo.setShareStatus(record.getShareStatus());
        return vo;
    }

    /**
     * 保存分享和分析文件的关联关系
     * @param context
     */
    private void saveShareFiles(CreateShareUrlContext context) {
        SaveShareFilesContext saveShareFilesContext = new SaveShareFilesContext();
        saveShareFilesContext.setShareId(context.getRecord().getShareId());
        saveShareFilesContext.setShareFileIdList(context.getShareFileIdList());
        saveShareFilesContext.setUserId(context.getUserId());
        panShareFileService.saveShareFiles(saveShareFilesContext);
    }

    /**
     * 拼装分享的实体，并保存到数据库中
     * @param context
     */
    private void saveShare(CreateShareUrlContext context) {
        PanShare record = new PanShare();

        record.setShareId(IdUtil.get());
        record.setShareName(context.getShareName());
        record.setShareType(context.getShareType());
        record.setShareDayType(context.getShareDayType());

        Integer shareDay = ShareDayTypeEnum.getShareDayByCode(context.getShareDayType());
        if (ObjectUtil.equals(BtboxConstants.MINUS_ONE_INT, shareDay)) {
            throw new ServiceException("分享天数非法");
        }
        record.setShareDay(shareDay);
        record.setShareEndTime(DateUtil.offsetDay(new Date(), shareDay));
        record.setShareUrl(createShareUrl(record.getShareId()));
        record.setShareCode(createShareCode());
        record.setCreateUser(context.getUserId());
        record.setCreateTime(new Date());
        if (!this.save(record)) {
            throw new ServiceException("保存分享信息失败");
        }
        context.setRecord(record);
    }

    /**
     * 创建分享的分享码
     * @return
     */
    private String createShareCode() {
        return RandomStringUtils.randomAlphabetic(4).toLowerCase();
    }

    /**
     * 创建分享的URL
     * @param shareId
     * @return
     */
    private String createShareUrl(Long shareId) {
        if (ObjectUtil.isNull(shareId)) {
            throw new ServiceException("分享的ID不能为空");
        }
        String sharePrefix = panServerConfig.getSharePrefix();
        if (sharePrefix.lastIndexOf(BtboxConstants.SLASH_STR) == BtboxConstants.MINUS_ONE_INT) {
            sharePrefix += BtboxConstants.SLASH_STR;
        }
        return sharePrefix + shareId;
    }
}
