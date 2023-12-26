package org.btbox.pan.services.modules.user.convert;

import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.domain.vo.UserInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 9:21
 * @version: 1.0
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserConvert {

    /**
     * 拼装用户基本信息返回实体
     *
     * @param btboxPanUser
     * @param userFile
     * @return
     */
    @Mapping(source = "btboxPanUser.username", target = "username")
    @Mapping(source = "userFile.fileId", target = "rootFileId")
    @Mapping(source = "userFile.filename", target = "rootFilename")
    UserInfoVO assembleUserInfoVO(BtboxPanUser btboxPanUser, UserFile userFile);


}
