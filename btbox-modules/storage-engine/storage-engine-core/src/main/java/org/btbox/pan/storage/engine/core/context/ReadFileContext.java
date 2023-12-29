package org.btbox.pan.storage.engine.core.context;

import lombok.Data;

import java.io.OutputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件读取的上下文信息
 * @author: BT-BOX
 * @createDate: 2023/12/29 14:58
 * @version: 1.0
 */
@Data
public class ReadFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -5614613667634069184L;

    /**
     * 文件的真实存储路径
     */
    private String realPath;

    /**
     * 文件的输出流
     */
    private OutputStream outputStream;
}