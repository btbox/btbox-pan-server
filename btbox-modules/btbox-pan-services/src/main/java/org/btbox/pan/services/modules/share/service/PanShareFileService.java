package org.btbox.pan.services.modules.share.service;

import org.btbox.pan.services.modules.share.domain.context.SaveShareFilesContext;
import org.btbox.pan.services.modules.share.domain.entity.PanShareFile;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/16 10:38
 * @version: 1.0
*/
public interface PanShareFileService extends IService<PanShareFile>{

    /**
     * 保存分享的文件和对应关系
     * @author: BT-BOX(HJH)
     * @param
     * @version: 1.0
     * @createDate: 2024/1/16 14:34
     * @return: void
     */
    void saveShareFiles(SaveShareFilesContext context);

}
