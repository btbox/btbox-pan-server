package org.btbox.pan.storage.engine.core;

import cn.hutool.core.lang.Assert;
import org.btbox.pan.storage.engine.core.context.DeleteFileContext;
import org.btbox.pan.storage.engine.core.context.StoreFileContext;

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