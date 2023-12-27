package org.btbox.pan.storage.engine.core;

import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.btbox.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;

/**
 * @description: 文件存储引擎顶级接口
 * @author: BT-BOX
 * @createDate: 2023/12/27 11:43
 * @version: 1.0
 */
public interface StorageEngine {

    /**
     * 存储物理文件
     * @param context
     * @throws IOException
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     * @param context
     * @throws IOException
     */
    void delete(DeleteFileContext context) throws IOException;

}
