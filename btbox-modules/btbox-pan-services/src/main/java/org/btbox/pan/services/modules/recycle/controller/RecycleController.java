package org.btbox.pan.services.modules.recycle.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.domain.R;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.recycle.domain.context.QueryRecycleFileListContext;
import org.btbox.pan.services.modules.recycle.service.RecycleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
public class RecycleController {

    private final RecycleService recycleService;

    @Schema(title = "获取回收站文件列表", description = "该接口提供了获取回收站文件列表的功能")
    public R<List<UserFileVO>> recycles() {
        QueryRecycleFileListContext context = new QueryRecycleFileListContext();
        context.setUserId(LoginHelper.getUserId());
        List<UserFileVO> result = recycleService.recycles(context);
        return R.ok(result);
    }

}