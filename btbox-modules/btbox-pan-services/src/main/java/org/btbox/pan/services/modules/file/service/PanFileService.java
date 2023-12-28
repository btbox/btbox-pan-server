package org.btbox.pan.services.modules.file.service;

import org.btbox.pan.services.modules.file.domain.context.FileChunkMergeAndSaveContext;
import org.btbox.pan.services.modules.file.domain.context.FileSaveContext;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:31
 * @version: 1.0
 */
public interface PanFileService extends IService<PanFile> {

    /**
     * 上传单文件并保存实体记录
     *
     * @param context
     * @author: BT-BOX(HJH)
     * @version: 1.0
     * @createDate: 2023/12/27 14:59
     * @return: void
     */
    void saveFile(FileSaveContext context);

    /**
     * 合并物理文件并保存物理文件记录
     * @param context
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context);
}
