package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 10:16
 * @version: 1.0
 */
@Schema(title = "文件秒传参数实体")
@Data
public class SecUploadFileBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4716032418185532275L;

    @Schema(title = "文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件夹ID不能为空")
    private String parentId;

    @Schema(title = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @Schema(title = "文件的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}