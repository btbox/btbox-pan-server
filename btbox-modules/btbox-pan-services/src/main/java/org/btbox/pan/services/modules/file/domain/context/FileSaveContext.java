package org.btbox.pan.services.modules.file.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.PanFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 保存单文件的上下文实体
 * @author: BT-BOX
 * @createDate: 2023/12/27 14:59
 * @version: 1.0
 */
@Data
public class FileSaveContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 2009684819505801226L;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 文件的总大小
     */
    private Long totalSize;

    /**
     * 文件实体
     */
    private MultipartFile file;

    /**
     * 实体文件记录
     */
    private PanFile record;

    /**
     * 上传文件的物理路径
     */
    private String realPath;
}