package org.btbox.pan.storage.engine.oss;

import org.btbox.pan.storage.engine.core.AbstractStorageEngine;
import org.btbox.pan.storage.engine.core.context.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description: 基于OSS的文件存储引擎实现
 * @author: BT-BOX
 * @createDate: 2023/12/27 11:46
 * @version: 1.0
 */
@Component
public class OSSStorageEngine extends AbstractStorageEngine {
    /**
     * 执行保存文件分片
     * 下沉到底层去实现
     *
     * @param context
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {

    }

    /**
     * 执行删除物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     *
     * @param context
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {

    }

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {

    }

    /**
     * 执行文件分片的动作
     * 下沉到底层去实现
     *
     * @param context
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {

    }
}