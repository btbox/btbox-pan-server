package org.btbox.pan.services.modules.share.convert;

import org.assertj.core.util.Lists;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.common.satoken.utils.LoginHelper;
import org.btbox.pan.services.modules.file.domain.bo.*;
import org.btbox.pan.services.modules.file.domain.context.*;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.file.domain.vo.FolderTreeNodeVO;
import org.btbox.pan.services.modules.share.domain.bo.CreateShareUrlBO;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
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
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, imports = {LoginHelper.class, IdUtil.class, Lists.class})
public interface ShareConvert {

    @Mapping(target = "userId", expression = "java(LoginHelper.getUserId())")
    CreateShareUrlContext createShareUrlBO2CreateShareUrlContext(CreateShareUrlBO createShareUrlBO);

}
