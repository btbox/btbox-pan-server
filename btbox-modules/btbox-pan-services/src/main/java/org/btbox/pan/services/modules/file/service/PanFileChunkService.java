package org.btbox.pan.services.modules.file.service;

import org.btbox.pan.services.modules.file.domain.context.FileChunkSaveContext;
import org.btbox.pan.services.modules.file.domain.entity.PanFileChunk;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:09
 * @version: 1.0
 */
public interface PanFileChunkService extends IService<PanFileChunk> {

    /**
     * 文件分片保存
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/28 15:15
     * @return: void
     */
    void saveChunkFile(FileChunkSaveContext context);
}
