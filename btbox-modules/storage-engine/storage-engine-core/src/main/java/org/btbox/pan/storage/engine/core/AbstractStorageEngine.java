package org.btbox.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import org.btbox.pan.storage.engine.core.context.*;

import java.io.IOException;

/**
 * @description: 顶级文件存储引擎的公用父类
 * @author: BT-BOX
 * @createDate: 2023/12/27 11:43
 * @version: 1.0
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Override
    public void store(StoreFileContext context) throws IOException {
        checkStoreFileContext(context);
        doStore(context);
    }

    /**
     * 删除物理文件
     * 1. 参数校验
     * 2. 执行动作
     * @param context
     * @throws IOException
     */
    @Override
    public void delete(DeleteFileContext context) throws IOException {
        checkDeleteFileContext(context);
        doDelete(context);
    }

    /**
     * 存储物理文件的分片
     * 1. 参数校验
     * 2. 执行动作
     * @param context
     * @throws IOException
     */
    @Override
    public void storeChunk(StoreFileChunkContext context) throws IOException {
        checkStoreFileChunkContext(context);
        doStoreChunk(context);
    }

    /**
     * 合并文件分片
     *
     * @param context
     * @throws IOException
     */
    @Override
    public void mergeFile(MergeFileContext context) throws IOException {
        checkMergeFileContext(context);
        doMergeFile(context);
    }

    /**
     * 读取文件内容写入到输出流中
     * 1. 参数校验
     * 2. 执行动作
     * @param context
     * @throws IOException
     */
    @Override
    public void readFile(ReadFileContext context) throws IOException {
        checkReadFileContext(context);
        doReadFile(context);
    }

    /**
     * 读取文件内容写入到输出流中
     * 下沉到底层去实现
     * @param context
     */
    protected abstract void doReadFile(ReadFileContext context) throws IOException;

    /**
     * 文件读取参数校验
     * @param context
     */
    private void checkReadFileContext(ReadFileContext context) {
        Assert.notBlank(context.getRealPath(), "文件真实存储路径不能为空");
        Assert.notNull(context.getOutputStream(), "文件输出流不能为空");
    }

    /**
     * 执行文件分片的动作
     * 下沉到底层去实现
     * @param context
     */
    protected abstract void doMergeFile(MergeFileContext context) throws IOException;

    /**
     * 校验文件分片合并的实体信息
     * @param context
     */
    private void checkMergeFileContext(MergeFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getUserId(), "当前登录的用户ID不能为空");
        Assert.notNull(context.getRealPathList(), "文件分片列表不能为空");
    }

    /**
     * 执行保存文件分片
     * 下沉到底层去实现
     * @param context
     */
    protected abstract void doStoreChunk(StoreFileChunkContext context) throws IOException;

    /**
     * 校验保存文件分片的参数
     * @param context
     */
    private void checkStoreFileChunkContext(StoreFileChunkContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notBlank(context.getIdentifier(), "文件唯一标识不能为空");
        Assert.notNull(context.getTotalSize(), "文件总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件输入流不能为空");
        Assert.notNull(context.getTotalChunks(), "文件分片总数不能为空");
        Assert.notNull(context.getChunkNumber(), "文件分片下标不能为空");
        Assert.notNull(context.getCurrentChunkSize(), "文件分片的大小不能为空");
        Assert.notNull(context.getUserId(), "当前登录的用户ID不能为空");
    }

    /**
     * 执行删除物理文件的动作
     * 下沉到具体的子类去实现
     * @param context
     */
    protected abstract void doDelete(DeleteFileContext context) throws IOException;


    private void checkDeleteFileContext(DeleteFileContext context) {
        Assert.notEmpty(context.getRealFilePathList(), "要删除的文件路径列表不能为空");
    }

    /**
     * 执行保存物理文件的动作
     * 下沉到具体的子类去实现
     * @param context
     */
    protected abstract void doStore(StoreFileContext context) throws IOException;

    /**
     * 校验上传物理文件的上下文信息
     * @param context
     */
    private void checkStoreFileContext(StoreFileContext context) {
        Assert.notBlank(context.getFilename(), "文件名称不能为空");
        Assert.notNull(context.getTotalSize(), "文件的总大小不能为空");
        Assert.notNull(context.getInputStream(), "文件不能为空");
    }

}