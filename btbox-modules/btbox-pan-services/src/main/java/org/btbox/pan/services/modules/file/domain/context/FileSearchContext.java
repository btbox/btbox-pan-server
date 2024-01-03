package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description: 搜索文件上下文实体
 * @author: BT-BOX
 * @createDate: 2024/1/3 10:44
 * @version: 1.0
 */
@Data
public class FileSearchContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 5299988116520422353L;

    /**
     * 搜索的关键字
     */
    private String keyword;

    /**
     * 搜索的文件类型集合
     */
    private List<Integer> fileTypeArray;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

}