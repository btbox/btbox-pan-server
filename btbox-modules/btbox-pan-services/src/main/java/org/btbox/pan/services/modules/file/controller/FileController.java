package org.btbox.pan.services.modules.file.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.BtboxConstants;
import org.btbox.common.core.constant.FileConstants;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.domain.model.LoginUser;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.convert.FileConvert;
import org.btbox.pan.services.modules.file.domain.bo.*;
import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.services.modules.file.domain.vo.FileChunkUploadVO;
import org.btbox.pan.services.modules.file.domain.vo.FolderTreeNodeVO;
import org.btbox.pan.services.modules.file.domain.vo.UploadedChunksVO;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    @Schema(title = "文件重命名", description = "该接口提供了文件重命名的功能")
    @PutMapping("rename")
    public R<Void> rename(@Validated @RequestBody UpdateFilenameBO updateFilenameBO) {
        UpdateFilenameContext context = fileConvert.updateFilenameBO2UpdateFilenameContext(updateFilenameBO);
        userFileService.updateFilename(context);
        return R.ok();
    }

    @Schema(title = "批量删除文件", description = "该接口提供了批量删除文件的功能")
    @DeleteMapping("delete")
    public R<Void> delete(@Validated @RequestBody DeleteFileBO deleteFileBO) {
        DeleteFileContext context = fileConvert.deleteFileBO2DeleteFileContext(deleteFileBO);
        String fileIds = deleteFileBO.getFileIds();
        List<Long> fileIdList = StrUtil.split(BtboxConstants.COMMON_SEPARATOR, fileIds).stream().map(IdUtil::decrypt).collect(Collectors.toList());
        context.setFileIdList(fileIdList);
        userFileService.deleteFile(context);
        return R.ok();
    }


    @Schema(title = "文件秒传", description = "该接口提供了文件秒传的功能")
    @PostMapping("sec-upload")
    public R<Void> secUpload(@Validated @RequestBody SecUploadFileBO secUploadFileBO) {
        SecUploadFileContext context = fileConvert.secUploadFileBO2secUploadFileContext(secUploadFileBO);
        boolean success = userFileService.secUpload(context);
        if (success) {
            return R.ok();
        }
        return R.fail("文件唯一标识不存在，请手动执行文件上传的操作");
    }

    @Schema(title = "单文件上传", description = "该接口提供了单文件上传的功能")
    @PostMapping("upload")
    public R<Void> upload(@Validated @RequestBody FileUploadBO fileUploadBO) {
        FileUploadContext context = fileConvert.fileUploadBO2FileUploadContext(fileUploadBO);
        userFileService.upload(context);
        return R.ok();
    }

    @Schema(title = "文件分片上传", description = "该接口提供了文件分片上传的功能")
    @PostMapping("chunk-upload")
    public R<FileChunkUploadVO> chunkUpload(@Validated @RequestBody FileChunkUploadBO fileChunkUploadBO) {
        FileChunkUploadContext context = fileConvert.fileChunkUploadBO2FileChunkUploadContext(fileChunkUploadBO);
        FileChunkUploadVO vo = userFileService.chunkUpload(context);
        return R.ok(vo);
    }

    @Schema(title = "查询已经上传的文件分片列表", description = "该接口提供了查询已经上传的文件分片列表的功能")
    @GetMapping("get-uploaded-chunks")
    public R<UploadedChunksVO> getUploadedChunks(@Validated QueryUploadedChunksBO queryUploadedChunksBO) {
        QueryUploadedChunksContext context = fileConvert.queryUploadedChunksBO2QueryUploadedChunksContext(queryUploadedChunksBO);
        UploadedChunksVO vo = userFileService.getUploadedChunks(context);
        return R.ok(vo);
    }

    @Schema(title = "文件分片合并", description = "该接口提供了文件分片合并的功能")
    @PostMapping("merge")
    public R<Void> mergeFile(@Validated @RequestBody FileChunkMergeBO fileChunkMergeBO) {
        FileChunkMergeContext context = fileConvert.fileChunkMergeBO2QueryFileChunkMergeContext(fileChunkMergeBO);
        userFileService.mergeFile(context);
        return R.ok();
    }

    @Schema(title = "文件分片合并", description = "该接口提供了文件分片合并的功能")
    @GetMapping("download")
    public void mergeFile(@NotBlank(message = "文件ID不能为空") String fileId, HttpServletResponse response
    ) {
        FileDownloadContext context = new FileDownloadContext();
        context.setFileId(IdUtil.decrypt(fileId));
        context.setUserId(LoginHelper.getUserId());
        context.setResponse(response);
        userFileService.download(context);
    }

    @Schema(title = "文件预览", description = "该接口提供了文件预览的功能")
    @GetMapping("preview")
    public void preview(@NotBlank(message = "文件ID不能为空") String fileId, HttpServletResponse response
    ) {
        FilePreviewContext context = new FilePreviewContext();
        context.setFileId(IdUtil.decrypt(fileId));
        context.setUserId(LoginHelper.getUserId());
        context.setResponse(response);
        userFileService.preview(context);
    }

    @Schema(title = "查询文件夹树", description = "该接口提供了查询文件夹树的功能")
    @GetMapping("folder/tree")
    public R<List<FolderTreeNodeVO>> getFolderTree() {
        QueryFolderTreeContext context = new QueryFolderTreeContext();
        context.setUserId(LoginHelper.getUserId());
        List<FolderTreeNodeVO> vo = userFileService.getFolderTree(context);
        return R.ok(vo);
    }

    @Schema(title = "文件转移", description = "该接口提供了文件转移的功能")
    @PostMapping("transfer")
    public R<Void> transfer(@Validated @RequestBody TransferFileBO transferFileBO) {
        String fileIds = transferFileBO.getFileIds();
        String targetParentId = transferFileBO.getTargetParentId();
        List<Long> fileIdList = Arrays.stream(StringUtils.split(BtboxConstants.COMMON_SEPARATOR, fileIds)).map(IdUtil::decrypt).toList();
        TransferFileContext context = new TransferFileContext();
        context.setFileIdList(fileIdList);
        context.setTargetParentId(IdUtil.decrypt(targetParentId));
        context.setUserId(LoginHelper.getUserId());
        userFileService.transfer(context);
        return R.ok();
    }

    @Schema(title = "文件复制", description = "该接口提供了文件复制的功能")
    @PostMapping("copy")
    public R<Void> transfer(@Validated @RequestBody CopyFileBO copyFileBO) {
        String fileIds = copyFileBO.getFileIds();
        String targetParentId = copyFileBO.getTargetParentId();
        List<Long> fileIdList = Arrays.stream(StringUtils.split(BtboxConstants.COMMON_SEPARATOR, fileIds)).map(IdUtil::decrypt).toList();
        CopyFileContext context = new CopyFileContext();
        context.setFileIdList(fileIdList);
        context.setTargetParentId(IdUtil.decrypt(targetParentId));
        context.setUserId(LoginHelper.getUserId());
        userFileService.copy(context);
        return R.ok();
    }

}