package org.btbox.pan.services.modules.recycle.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件还原参数实体
 * @author: BT-BOX
 * @createDate: 2024/1/4 17:03
 * @version: 1.0
 */
@Schema(title = "文件还原参数实体")
@Data
public class RestoreBO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4883129412675864635L;

    @Schema(title = "要还原的文件ID集合, 多个使用公用分隔符分隔", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要还原的文件")
    private String fileIds;

}