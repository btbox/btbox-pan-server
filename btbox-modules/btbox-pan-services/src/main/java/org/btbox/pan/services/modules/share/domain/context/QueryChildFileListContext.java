package org.btbox.pan.services.modules.share.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;

/**
 * @description: 查询下一级文件列表的上下文实体信息
 * @author: BT-BOX
 * @createDate: 2024/1/25 10:26
 * @version: 1.0
 */
@Data
public class QueryChildFileListContext {

    /**
     * 分析的ID
     */
    private Long shareId;

    /**
     * 父文件夹的ID
     */
    private Long parentId;

    /**
     * 分享信息对应的实体信息
     */
    private PanShare record;

}