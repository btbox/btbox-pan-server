package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 查询用户已上传的分片列表的上下文信息实体
 * @author: BT-BOX
 * @createDate: 2023/12/28 16:27
 * @version: 1.0
 */
@Data
public class QueryUploadedChunksContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 6846252924877294455L;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}