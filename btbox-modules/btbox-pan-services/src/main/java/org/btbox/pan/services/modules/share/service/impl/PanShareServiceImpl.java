package org.btbox.pan.services.modules.share.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.DateUtils;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.common.config.PanServerConfig;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
import org.btbox.pan.services.modules.share.domain.context.SaveShareFilesContext;
import org.btbox.pan.services.modules.share.domain.vo.RPanShareUrlVO;
import org.btbox.pan.services.modules.share.enums.ShareDayTypeEnum;
import org.btbox.pan.services.modules.share.service.PanShareFileService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.btbox.pan.services.modules.share.repository.mapper.PanShareMapper;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.springframework.transaction.annotation.Transactional;

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
    public RPanShareUrlVO create(CreateShareUrlContext context) {
        saveShare(context);
        saveShareFiles(context);
        return assembleShareVO(context);
    }

    /***************************************** private ****************************************/

    /**
     * 拼装对应的返回VO
     * @param context
     * @return
     */
    private RPanShareUrlVO assembleShareVO(CreateShareUrlContext context) {
        PanShare record = context.getRecord();
        RPanShareUrlVO vo = new RPanShareUrlVO();
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
