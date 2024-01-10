package org.btbox.pan.services.modules.recycle.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;

import java.util.List;

/**
 * @description: 文件删除上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/10 16:18
 * @version: 1.0
 */
@Data
public class DeleteContext {

    /**
     * 要操作的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 要删除的文件记录列表
     */
    private List<UserFile> records;

    /**
     * 所有要被删除的文件记录列表
     */
    private List<UserFile> allRecords;


}