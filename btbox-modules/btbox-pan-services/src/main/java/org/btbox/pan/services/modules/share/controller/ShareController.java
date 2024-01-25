package org.btbox.pan.services.modules.share.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.common.annotation.NeedShareCode;
import org.btbox.pan.services.common.utils.ShareIdUtil;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.share.convert.CancelShareContext;
import org.btbox.pan.services.modules.share.convert.ShareConvert;
import org.btbox.pan.services.modules.share.domain.bo.CancelShareBO;
import org.btbox.pan.services.modules.share.domain.bo.CheckShareCodeBO;
import org.btbox.pan.services.modules.share.domain.bo.CreateShareUrlBO;
import org.btbox.pan.services.modules.share.domain.context.*;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;
import org.btbox.pan.services.modules.share.domain.vo.ShareDetailVO;
import org.btbox.pan.services.modules.share.domain.vo.ShareSimpleDetailVO;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 9:26
 * @version: 1.0
 */
@RestController
@Schema(title = "分析模块")
@Validated
@RequiredArgsConstructor
@RequestMapping("share")
public class ShareController {

    private final PanShareService panShareService;

    private final ShareConvert shareConvert;

    @Operation(summary = "创建分析链接", description = "该接口提供了创建分析链接的功能")
    @PostMapping("create")
    public R<PanShareUrlVO> create(@Validated @RequestBody CreateShareUrlBO createShareUrlBO) {
        CreateShareUrlContext context = shareConvert.createShareUrlBO2CreateShareUrlContext(createShareUrlBO);
        String shareFileIds = createShareUrlBO.getShareFileIds();
        List<Long> shareFileIdList = StrUtil.split(shareFileIds, BtboxConstants.COMMON_SEPARATOR).stream().map(IdUtil::decrypt).toList();
        context.setShareFileIdList(shareFileIdList);

        PanShareUrlVO vo = panShareService.create(context);
        return R.ok(vo);
    }

    @Operation(summary = "查询分享链接列表", description = "该接口提供了查询分享链接列表的功能")
    @GetMapping("shares")
    public R<List<PanShareUrlListVO>> getShares() {
        QueryShareListContext context = new QueryShareListContext();
        context.setUserId(LoginHelper.getUserId());
        List<PanShareUrlListVO> result = panShareService.getShares(context);
        return R.ok(result);
    }

    @Operation(summary = "取消分享", description = "该接口提供了取消分享的功能")
    @DeleteMapping("cancel")
    public R<Void> cancelShare(@Validated @RequestBody CancelShareBO cancelShareBO) {
        CancelShareContext context = new CancelShareContext();
        context.setUserId(LoginHelper.getUserId());
        String shareIds = cancelShareBO.getShareIds();
        List<Long> shareIdList = StrUtil.split(shareIds, BtboxConstants.COMMON_SEPARATOR).stream().map(IdUtil::decrypt).toList();
        context.setShareIdList(shareIdList);
        panShareService.cancelShare(context);
        return R.ok();
    }

    @Operation(summary = "校验分享码", description = "该接口提供了校验分享码的功能")
    @SaIgnore
    @PostMapping("code/check")
    public R<String> checkShareCode(@Validated @RequestBody CheckShareCodeBO checkShareCodeBO) {
        CheckShareCodeContext context = new CheckShareCodeContext();
        context.setShareId(IdUtil.decrypt(checkShareCodeBO.getShareId()));
        context.setShareCode(checkShareCodeBO.getShareCode());
        String token = panShareService.checkShareCode(context);
        return R.ok(token);
    }

    @Operation(summary = "查看分享详情", description = "该接口提供了查看分享详情的功能")
    @SaIgnore
    @NeedShareCode
    @PostMapping("detail")
    public R<ShareDetailVO> detail() {
        QueryShareDetailContext context = new QueryShareDetailContext();
        context.setShareId(ShareIdUtil.get());
        ShareDetailVO vo = panShareService.detail(context);
        return R.ok(vo);
    }

    @Operation(summary = "查看分享简单详情", description = "该接口提供了查看分享简单详情的功能")
    @SaIgnore
    @GetMapping("simple")
    public R<ShareSimpleDetailVO> simpleDetail(@NotBlank(message = "分享的ID不能为空") String shareId) {
        QueryShareSimpleDetailContext context = new QueryShareSimpleDetailContext();
        context.setShareId(IdUtil.decrypt(shareId));
        ShareSimpleDetailVO vo = panShareService.simpleDetail(context);
        return R.ok(vo);
    }

    @Operation(summary = "获取下一级文件列表", description = "该接口提供了获取下一级文件列表的功能")
    @SaIgnore
    @GetMapping("file/list")
    public R<List<UserFileVO>> fileList(@NotBlank(message = "文件的父ID不能为空") String parentId) {
        QueryChildFileListContext context = new QueryChildFileListContext();
        context.setShareId(ShareIdUtil.get());
        context.setParentId(IdUtil.decrypt(parentId));

        List<UserFileVO> vo = panShareService.fileList(context);
        return R.ok(vo);
    }

}