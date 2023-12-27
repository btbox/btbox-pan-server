package org.btbox.pan.storage.engine.core.context;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 15:37
 * @version: 1.0
 */
@Data
public class DeleteFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 3728982929761112321L;

    /**
     * 要删除的物理文件路径集合
     */
    private List<String> realFilePathList;
}