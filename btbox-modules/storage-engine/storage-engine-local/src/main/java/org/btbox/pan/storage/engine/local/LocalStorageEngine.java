package org.btbox.pan.storage.engine.local;

import lombok.RequiredArgsConstructor;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.pan.storage.engine.core.AbstractStorageEngine;
import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.btbox.pan.storage.engine.core.context.StoreFileContext;
import org.btbox.pan.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @description: 基于本地的文件存储引擎实现
 * @author: BT-BOX
 * @createDate: 2023/12/27 11:46
 * @version: 1.0
 */
@Component
@RequiredArgsConstructor
public class LocalStorageEngine extends AbstractStorageEngine {

    private final LocalStorageEngineConfig config;
    
    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
    }


    /**
     * 执行删除物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtils.deleteFiles(context.getRealFilePathList());
    }
}