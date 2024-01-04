package org.btbox.pan.services.modules.recycle.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 要还原的实体上下文
 * @author: BT-BOX
 * @createDate: 2024/1/4 17:05
 * @version: 1.0
 */
@Data
public class RestoreContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 4681365007229461486L;

    /**
     * 要操作的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要还原的文件记录列表
     */
    private List<UserFile> records;

}