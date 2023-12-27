package org.btbox.pan.services.modules.file.domain.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:24
 * @version: 1.0
 */
@Data
public class SecUploadFileContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 3714432999617546232L;

    /**
     * 文件夹ID
     */
    private Long parentId;

    /**
     * 文件名称
     */
    private String filename;

    /**
     * 文件的唯一标识
     */
    private String identifier;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

}