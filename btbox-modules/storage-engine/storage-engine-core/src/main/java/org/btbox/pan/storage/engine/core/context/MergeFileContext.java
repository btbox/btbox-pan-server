package org.btbox.pan.storage.engine.core.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 合并文件上下文对象
 * @author: BT-BOX
 * @createDate: 2023/12/29 9:11
 * @version: 1.0
 */
@Data
public class MergeFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 416216157648089723L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 文件分片的真实存储路径集合
     */
    private List<String> realPathList;

    /**
     * 文件合并后的真实物理存储路径
     */
    private String realPath;
}