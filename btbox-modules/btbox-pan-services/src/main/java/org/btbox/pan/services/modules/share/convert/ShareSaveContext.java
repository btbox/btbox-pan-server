package org.btbox.pan.services.modules.share.convert;

import lombok.Data;

import java.util.List;

/**
 * @description: 保存到我的网盘上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/25 11:46
 * @version: 1.0
 */
@Data
public class ShareSaveContext {

    /**
     * 要保存的文件ID列表
     */
    private List<Long> fileIdList;

    /**
     * 目标文件夹ID
     */
    private Long targetParentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享的ID
     */
    private Long shareId;

}