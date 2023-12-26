package org.btbox.pan.services.modules.file.domain.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件重命名参数的上下文对象
 * @author: BT-BOX
 * @createDate: 2023/12/26 17:18
 * @version: 1.0
 */
@Data
public class UpdateFilenameContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 8970064642793552358L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 新文件名称
     */
    private String newFilename;

    /**
     * 登录的用户id
     */
    private Long userId;

    /**
     * 更新的文件实体
     */
    private UserFile entity;
}