package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件分片合并上下文对象
 * @author: BT-BOX
 * @createDate: 2023/12/28 17:19
 * @version: 1.0
 */
@Data
public class FileChunkMergeContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 1532552677245257968L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件总大小
     */
    private Long totalSize;

    /**
     * 文件的父文件夹ID
     */
    private Long parentId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 物理文件记录
     */
    private PanFile record;

}