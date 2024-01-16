package org.btbox.pan.services.modules.share.domain.context;

import lombok.Data;

import java.util.List;

/**
 * @description: 保存文件和分享的关联关系上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/16 14:36
 * @version: 1.0
 */
@Data
public class SaveShareFilesContext {

    /**
     * 分析的ID
     */
    private Long shareId;

    /**
     * 分析对应的文件的ID集合
     */
    private List<Long> shareFileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

}