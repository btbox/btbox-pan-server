package org.btbox.pan.services.modules.share.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.btbox.pan.services.modules.share.domain.vo.ShareSimpleDetailVO;

/**
 * @description: 查询分享简单详情上下文实体信息
 * @author: BT-BOX
 * @createDate: 2024/1/17 11:19
 * @version: 1.0
 */
@Data
public class QueryShareSimpleDetailContext {

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享对应实体的信息
     */
    private PanShare record;

    /**
     * 简单分享详情的VO对象
     */
    private ShareSimpleDetailVO vo;

}