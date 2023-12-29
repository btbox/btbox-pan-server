package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 查询文件夹树的上下文实体信息
 * @author: BT-BOX
 * @createDate: 2023/12/29 17:47
 * @version: 1.0
 */
@Data
public class QueryFolderTreeContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -5412848336197388759L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

}