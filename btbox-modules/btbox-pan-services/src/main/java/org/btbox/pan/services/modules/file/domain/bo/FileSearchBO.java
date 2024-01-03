package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件搜索参数实体
 * @author: BT-BOX
 * @createDate: 2024/1/3 10:39
 * @version: 1.0
 */
@Schema(title = "文件搜索参数实体")
@Data
public class FileSearchBO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9125925139574182433L;

    @Schema(title = "搜索的关键字", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "搜索关键字不能为空")
    private String keyword;

    @Schema(title = "文件类型，多个文件类型用公用分隔符拼接")
    private String fileTypes;
}