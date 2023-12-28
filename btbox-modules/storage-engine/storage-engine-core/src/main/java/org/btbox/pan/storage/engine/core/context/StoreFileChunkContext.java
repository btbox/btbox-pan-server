package org.btbox.pan.storage.engine.core.context;

import lombok.Data;

import java.io.InputStream;
import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 保存文件分片的上下文信息
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:41
 * @version: 1.0
 */
@Data
public class StoreFileChunkContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 3407714327380339970L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件真实存储路径
     */
    private String realPath;

    /**
     * 文件总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片的下标
     */
    private Integer chunkNumber;

    /**
     * 当前分片的大小瓶
     */
    private Long currentChunkSize;

    /**
     * 当前登录用户的ID
     */
    private Long userId;
}