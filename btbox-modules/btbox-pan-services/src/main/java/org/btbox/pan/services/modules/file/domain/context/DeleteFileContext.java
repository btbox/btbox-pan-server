package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 9:05
 * @version: 1.0
 */
@Data
public class DeleteFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 3167921974561423060L;

    /**
     * 要删除的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前的登录用户ID
     */
    private Long userId;
}