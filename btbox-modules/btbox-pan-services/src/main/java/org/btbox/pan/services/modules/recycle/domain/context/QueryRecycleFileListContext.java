package org.btbox.pan.services.modules.recycle.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 查询用户回收站文件列表的上下文
 * @author: BT-BOX
 * @createDate: 2024/1/4 16:13
 * @version: 1.0
 */
@Data
public class QueryRecycleFileListContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -4581986777209931657L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;
}