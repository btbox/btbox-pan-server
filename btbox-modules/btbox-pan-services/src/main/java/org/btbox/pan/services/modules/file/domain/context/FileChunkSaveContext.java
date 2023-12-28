package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;
import org.btbox.common.core.enums.MergeFlagEnum;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:11
 * @version: 1.0
 */
@Data
public class FileChunkSaveContext implements Serializable {
    @Serial
    private static final long serialVersionUID = -172786691103973122L;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件唯一标识
     */
    private String identifier;

    /**
     * 文件的总分片数
     */
    private Integer totalChunks;

    /**
     * 当前分片下标
     * 从1开始
     */
    private Integer chunkNumber;

    /**
     * 当前分片的大小
     */
    private Long currentChunkSize;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 分片文件实体
     */
    private MultipartFile file;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 文件合并标识
     */
    private MergeFlagEnum mergeFlagEnum = MergeFlagEnum.NOT_READY;

    /**
     * 文件分片的真实存储路径
     */
    private String realPath;
}