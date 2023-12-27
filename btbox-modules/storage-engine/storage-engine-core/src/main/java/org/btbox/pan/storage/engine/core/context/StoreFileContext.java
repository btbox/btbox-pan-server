package org.btbox.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件存储引擎存储物理文件的上下文实体
 * @author: BT-BOX
 * @createDate: 2023/12/27 15:35
 * @version: 1.0
 */
@Data
public class StoreFileContext implements Serializable {

    @Serial
    private static final long serialVersionUID = -75118285444926766L;
    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件的输入流信息
     */
    private InputStream inputStream;

    /**
     * 文件上传后的物理路径
     */
    private String realPath;

}