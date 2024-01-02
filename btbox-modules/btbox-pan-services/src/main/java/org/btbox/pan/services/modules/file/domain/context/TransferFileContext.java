package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 文件转移上下文信息实体
 * @author: BT-BOX
 * @createDate: 2024/1/2 11:06
 * @version: 1.0
 */
@Data
public class TransferFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -6018762669220995585L;

    /**
     * 要转移的文件ID集合
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
     * 要转移的文件列表
     */
    private List<UserFile> prepareRecords;
}