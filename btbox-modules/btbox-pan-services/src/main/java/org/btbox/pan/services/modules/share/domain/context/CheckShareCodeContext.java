package org.btbox.pan.services.modules.share.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;

/**
 * @description: 校验分享码的上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/16 16:42
 * @version: 1.0
 */
@Data
public class CheckShareCodeContext {

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享码
     */
    private String shareCode;

    /**
     * 文件实体对象
     */
    PanShare record;

}