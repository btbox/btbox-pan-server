package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 搜索文件面包屑列表的上下文信息实体
 * @author: BT-BOX
 * @createDate: 2024/1/3 15:11
 * @version: 1.0
 */
@Data
public class QueryBreadcrumbsContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -446142137584490418L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 当前登录用户ID
     */
    private Long userId;
}