package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件复制参数实体
 * @author: BT-BOX
 * @createDate: 2024/1/3 9:47
 * @version: 1.0
 */
@Schema(title = "文件复制参数实体")
@Data
public class CopyFileBO implements Serializable {
    @Serial
    private static final long serialVersionUID = -108847019568682776L;

    @Schema(title = "要复制的文件ID集合，多个使用公用分隔符隔开", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要复制的文件")
    private String fileIds;

    @Schema(title = "要转移的目标文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要转移到哪个文件夹下面")
    private String targetParentId;
}