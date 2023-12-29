package org.btbox.pan.storage.engine.local;

import lombok.RequiredArgsConstructor;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.pan.storage.engine.core.AbstractStorageEngine;
import org.btbox.pan.storage.engine.core.context.*;
import org.btbox.pan.storage.engine.local.config.LocalStorageEngineConfig;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

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
        context.setRealPath(realFilePath);
    }


    /**
     * 执行保存文件分片
     * 下沉到底层去实现
     *
     * @param context
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        String basePath = config.getRootFileChunkPath();
        String realFilePath = FileUtils.generateStoreFileChunkRealPath(basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
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

    @Override
    protected void doReadFile(ReadFileContext context) throws IOException {
        File file = new File(context.getRealPath());
        FileUtils.writeFile2OutputStream(new FileInputStream(file), context.getOutputStream(), file.length());
    }

    /**
     * 执行文件分片的动作
     * 下沉到底层去实现
     *
     * @param context
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtils.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtils.createFile(new File(realFilePath));
        List<String> chunkPaths = context.getRealPathList();
        for (String chunkPath : chunkPaths) {
            FileUtils.appendWrite(Paths.get(realFilePath), new File(chunkPath).toPath());
        }
        FileUtils.deleteFiles(chunkPaths);
        context.setRealPath(realFilePath);
    }
}