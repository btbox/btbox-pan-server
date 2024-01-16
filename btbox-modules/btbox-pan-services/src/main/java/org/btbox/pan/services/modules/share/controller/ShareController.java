package org.btbox.pan.services.modules.share.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.share.convert.ShareConvert;
import org.btbox.pan.services.modules.share.domain.bo.CreateShareUrlBO;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
import org.btbox.pan.services.modules.share.domain.context.QueryShareListContext;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;
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

}