package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 9:02
 * @version: 1.0
 */
@Schema(title = "批量删除文件实体参数")
@Data
public class DeleteFileBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 6214850927405175046L;

    @Schema(title = "要删除的文件ID，多个用公用的分隔符分割", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;



}