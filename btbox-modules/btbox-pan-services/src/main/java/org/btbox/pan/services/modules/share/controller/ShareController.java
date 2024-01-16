package org.btbox.pan.services.modules.share.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.modules.share.convert.ShareConvert;
import org.btbox.pan.services.modules.share.domain.bo.CreateShareUrlBO;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
import org.btbox.pan.services.modules.share.domain.vo.RPanShareUrlVO;
import org.btbox.pan.services.modules.share.service.PanShareService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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
    public R<RPanShareUrlVO> create(@Validated @RequestBody CreateShareUrlBO createShareUrlBO) {
        CreateShareUrlContext context = shareConvert.createShareUrlBO2CreateShareUrlContext(createShareUrlBO);
        String shareFileIds = createShareUrlBO.getShareFileIds();
        List<Long> shareFileIdList = StrUtil.split(shareFileIds, BtboxConstants.COMMON_SEPARATOR).stream().map(IdUtil::decrypt).toList();
        context.setShareFileIdList(shareFileIdList);

        RPanShareUrlVO vo = panShareService.create(context);
        return R.ok(vo);
    }

}