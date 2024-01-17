package org.btbox.pan.services.modules.share.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.btbox.pan.services.modules.share.domain.vo.ShareDetailVO;

/**
 * @description: 查询分享详情的上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/17 9:39
 * @version: 1.0
 */
@Data
public class QueryShareDetailContext {

    /**
     * 分享的ID
     */
    private Long shareId;

    /**
     * 分享实体
     */
    private PanShare record;

    /**
     * 分享详情的VO对象
     */
    private ShareDetailVO vo;

}