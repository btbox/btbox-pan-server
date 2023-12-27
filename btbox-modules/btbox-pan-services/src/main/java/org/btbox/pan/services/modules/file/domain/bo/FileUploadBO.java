package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 14:38
 * @version: 1.0
 */
@Schema(title = "单文件上传参数实体对象")
@Data
public class FileUploadBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4824378243621054423L;

    @Schema(title = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @Schema(title = "文件的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;

    @Schema(title = "文件的总大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件的总大小不能为空")
    private Long totalSize;

    @Schema(title = "文件的父文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的父文件夹ID不能为空")
    private String parentId;

    @Schema(title = "文件实体", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件实体不能为空")
    private MultipartFile file;
}