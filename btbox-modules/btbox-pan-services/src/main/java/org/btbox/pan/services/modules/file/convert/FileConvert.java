package org.btbox.pan.services.modules.file.convert;

import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.domain.bo.CreateFolderBO;
import org.btbox.pan.services.modules.file.domain.bo.DeleteFileBO;
import org.btbox.pan.services.modules.file.domain.bo.SecUploadFileBO;
import org.btbox.pan.services.modules.file.domain.bo.UpdateFilenameBO;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.domain.context.DeleteFileContext;
import org.btbox.pan.services.modules.file.domain.context.SecUploadFileContext;
import org.btbox.pan.services.modules.file.domain.context.UpdateFilenameContext;
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
}
