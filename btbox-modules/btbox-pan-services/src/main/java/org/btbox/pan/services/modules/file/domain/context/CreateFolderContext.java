package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/14 9:28
 * @version: 1.0
 */
@Data
public class CreateFolderContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 6002305944696937759L;

    /**
     * 父文件夹ID
     */
    private Long parentId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String folderName;


}