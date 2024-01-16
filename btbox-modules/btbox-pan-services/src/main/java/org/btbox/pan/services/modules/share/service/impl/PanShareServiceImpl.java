package org.btbox.pan.services.modules.share.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.enums.ResponseCode;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.core.utils.JwtUtil;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.pan.services.common.config.PanServerConfig;
import org.btbox.pan.services.modules.share.constants.ShareConstants;
import org.btbox.pan.services.modules.share.convert.CancelShareContext;
import org.btbox.pan.services.modules.share.domain.context.CheckShareCodeContext;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
import org.btbox.pan.services.modules.share.domain.context.QueryShareListContext;
import org.btbox.pan.services.modules.share.domain.context.SaveShareFilesContext;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.btbox.pan.services.modules.share.domain.entity.PanShareFile;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;
import org.btbox.pan.services.modules.share.enums.ShareDayTypeEnum;
import org.btbox.pan.services.modules.share.enums.ShareStatusEnum;
import org.btbox.pan.services.modules.share.repository.mapper.PanShareMapper;
import org.btbox.pan.services.modules.share.service.PanShareFileService;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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


    /***************************************** private ****************************************/

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
