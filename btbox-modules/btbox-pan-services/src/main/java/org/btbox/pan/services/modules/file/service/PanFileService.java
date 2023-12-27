package org.btbox.pan.services.modules.file.service;

import org.btbox.pan.services.modules.file.domain.context.FileSaveContext;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import com.baomidou.mybatisplus.extension.service.IService;
    /**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:31
 * @version: 1.0
*/
public interface PanFileService extends IService<PanFile>{

    /**
     * 上传单文件并保存实体记录
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/27 14:59
     * @return: void
     */
    void saveFile(FileSaveContext context);

}
