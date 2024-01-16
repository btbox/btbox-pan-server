package org.btbox.pan.services.modules.share.convert;

import lombok.Data;

import java.util.List;

/**
 * @description: 取消分享的上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/16 16:09
 * @version: 1.0
 */
@Data
public class CancelShareContext {

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要取消的分享ID集合
     */
    private List<Long> shareIdList;

}