package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 查询文件列表上下文实体
 * @author: BT-BOX
 * @createDate: 2023/12/26 10:32
 * @version: 1.0
 */
@Data
public class QueryFileListContext implements Serializable {

    @Serial
    private static final long serialVersionUID = -8989655966458937803L;
    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 文件类型的集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 当前的登录用户
     */
    private Long userId;

    /**
     * 文件的删除标识
     */
    private Integer delFlag;

}