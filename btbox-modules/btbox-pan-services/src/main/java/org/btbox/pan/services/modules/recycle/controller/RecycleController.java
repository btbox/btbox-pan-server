package org.btbox.pan.services.modules.recycle.controller;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.recycle.domain.bo.RestoreBO;
import org.btbox.pan.services.modules.recycle.domain.context.QueryRecycleFileListContext;
import org.btbox.pan.services.modules.recycle.domain.context.RestoreContext;
import org.btbox.pan.services.modules.recycle.service.RecycleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/4 16:08
 * @version: 1.0
 */
@RestController
@Schema(title = "回收站模块")
@Validated
@RequiredArgsConstructor
@RequestMapping("recycle")
public class RecycleController {

    private final RecycleService recycleService;

    @Schema(title = "获取回收站文件列表", description = "该接口提供了获取回收站文件列表的功能")
    @GetMapping("recycles")
    public R<List<UserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(LoginHelper.getUserId());
        List<UserFileVO> result = recycleService.recycles(context);
        return R.ok(result);
    }

    @Schema(title = "删除的文件批量还原", description = "该接口提供了删除的文件批量还原的功能")
    @PutMapping("restore")
    public R<Void> restore(@Validated @RequestBody RestoreBO restoreBO) {

        List<Long> fileIdList = StrUtil.split(restoreBO.getFileIds(), BtboxConstants.COMMON_SEPARATOR).stream().map(IdUtil::decrypt).toList();

        RestoreContext context = new RestoreContext();
        context.setFileIdList(fileIdList);
        context.setUserId(LoginHelper.getUserId());
        recycleService.restore(context);
        return R.ok();
    }

}