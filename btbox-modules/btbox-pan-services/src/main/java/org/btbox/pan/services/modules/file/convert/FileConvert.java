package org.btbox.pan.services.modules.file.convert;

import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.domain.bo.*;
import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.storage.engine.core.context.StoreFileChunkContext;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 15:29
 * @version: 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {LoginHelper.class, IdUtil.class})
public interface FileConvert {


    @Mapping(target = "parentId", expression = "java(IdUtil.decrypt(createFolderBO.getParentId()))")
    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    CreateFolderContext createFolderBO2CreateFolder(CreateFolderBO createFolderBO);

    @Mapping(target = "fileId", expression = "java(IdUtil.decrypt(updateFilenameBO.getFileId()))")
    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    UpdateFilenameContext updateFilenameBO2UpdateFilenameContext(UpdateFilenameBO updateFilenameBO);

    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    DeleteFileContext deleteFileBO2DeleteFileContext(DeleteFileBO deleteFileBO);

    @Mapping(target = "parentId", expression = "java(IdUtil.decrypt(secUploadFileBO.getParentId()))")
    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    SecUploadFileContext secUploadFileBO2secUploadFileContext(SecUploadFileBO secUploadFileBO);

    @Mapping(target = "parentId", expression = "java(IdUtil.decrypt(fileUploadBO.getParentId()))")
    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    FileUploadContext fileUploadBO2FileUploadContext(FileUploadBO fileUploadBO);

    @Mapping(target = "record", ignore = true)
    FileSaveContext fileUploadContext2FileSaveContext(FileUploadContext context);

    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    FileChunkUploadContext fileChunkUploadBO2FileChunkUploadContext(FileChunkUploadBO fileChunkUploadBO);

    FileChunkSaveContext fileChunkUploadContext2FileChunkSaveContext(FileChunkUploadContext context);

    @Mapping(target = "realPath", ignore = true)
    StoreFileChunkContext fileChunkSaveContext2StoreFileChunkContext(FileChunkSaveContext context);

}
