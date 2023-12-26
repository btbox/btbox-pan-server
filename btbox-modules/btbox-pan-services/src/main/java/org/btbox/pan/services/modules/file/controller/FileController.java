package org.btbox.pan.services.modules.file.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.modules.file.convert.FileConvert;
import org.btbox.pan.services.modules.file.domain.bo.CreateFolderBO;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.domain.context.QueryFileListContext;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 9:46
 * @version: 1.0
 */
@RequestMapping("file")
@RestController
@RequiredArgsConstructor
@SaIgnore
@Tag(name = "文件控制器")
@Validated
public class FileController {

    private final UserFileService userFileService;

    private final FileConvert fileConvert;

    @Operation(summary = "用户在线修改密码", description = "该接口提供了用户在线修改密码")
    @GetMapping("files")
    public R<List<UserFileVO>> list(
            @NotBlank(message = "父文件夹ID不能为空") @RequestParam(value = "parentId", required = false) String parentId,
            @RequestParam(value = "fileTypes", required = false, defaultValue = FileConstants.ALL_FILE_TYPE) String fileTypes
    ) {
        Long realParentId = IdUtil.decrypt(parentId);
        List<Integer> fileTypeArray = null;

        if (!ObjectUtil.equals(FileConstants.ALL_FILE_TYPE, fileTypes)) {
            fileTypeArray = StrUtil.split(BtboxConstants.COMMON_SEPARATOR, fileTypes).stream().map(Integer::valueOf).collect(Collectors.toList());
        }

        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setParentId(realParentId);
        queryFileListContext.setFileTypeArray(fileTypeArray);
        queryFileListContext.setUserId(StpUtil.getLoginIdAsLong());
        queryFileListContext.setDelFlag(DelFlagEnum.NO.getCode());

        return R.ok(userFileService.getFileList(queryFileListContext));
    }

    @Schema(title = "创建文件夹", description = "该接口提供了创建文件夹的功能")
    @PostMapping("folder")
    public R<String> createFolder(@Validated @RequestBody CreateFolderBO createFolderBO) {
        CreateFolderContext context = fileConvert.createFolderBO2CreateFolder(createFolderBO);
        Long fileId = userFileService.createFolder(context);
        return R.ok(IdUtil.encrypt(fileId));
    }

}