package org.btbox.pan.services.modules.file.service;

import org.btbox.pan.services.modules.file.domain.context.CreateFolderContext;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:15
 * @version: 1.0
*/
public interface UserFileService extends IService<UserFile>{

    /**
     * 创建文件夹信息
     * @author: BT-BOX(HJH)
     * @param createFolderContext
     * @version: 1.0
     * @createDate: 2023/12/14 9:25
     * @return: java.lang.Long
     */
    Long createFolder(CreateFolderContext createFolderContext);

    /**
     * 获取用户的根文件夹信息
     * @author: BT-BOX(HJH)
     * @param userId
     * @version: 1.0
     * @createDate: 2023/12/25 17:58
     * @return: org.btbox.pan.services.modules.file.domain.entity.UserFile
     */
    UserFile getUserRootFile(Long userId);
}
