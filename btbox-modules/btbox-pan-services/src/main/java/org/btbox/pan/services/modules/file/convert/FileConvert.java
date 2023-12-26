package org.btbox.pan.services.modules.file.convert;

import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.domain.bo.CreateFolderBO;
import org.btbox.pan.services.modules.file.domain.bo.UpdateFilenameBO;
import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
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
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {LoginHelper.class})
public interface FileConvert {


    @Mapping(target = "parentId", expression = "java(org.btbox.common.core.utils.IdUtil.decrypt(createFolderBO.getParentId()))")
    @Mapping(target = "userId", expression = "java(org.btbox.common.core.utils.IdUtil.decrypt(LoginHelper.getUserIdAsString()))")
    CreateFolderContext createFolderBO2CreateFolder(CreateFolderBO createFolderBO);

    @Mapping(target = "fileId", expression = "java(org.btbox.common.core.utils.IdUtil.decrypt(updateFilenameBO.getFileId()))")
    @Mapping(target = "userId", expression = "java(org.btbox.common.core.utils.IdUtil.decrypt(LoginHelper.getUserIdAsString()))")
    UpdateFilenameContext updateFilenameBO2UpdateFilenameContext(UpdateFilenameBO updateFilenameBO);
}
