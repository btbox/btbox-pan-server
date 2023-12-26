package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 17:16
 * @version: 1.0
 */
@Schema(title = "文件重命名参数对象")
@Data
public class UpdateFilenameBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5844724967527427524L;

    @Schema(title = "更新的文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "更新的文件ID不能为空")
    private String fileId;

    @Schema(title = "新的文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新的文件名称不能为空")
    private String newFilename;

}