package org.btbox.pan.services.modules.share.domain.context;

import lombok.Data;

/**
 * @description: 查询用户已有的分享链接列表的上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/16 15:18
 * @version: 1.0
 */
@Data
public class QueryShareListContext {

    /**
     * 当前登录的用户ID
     */
    private Long userId;

}