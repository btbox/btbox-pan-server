package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 16:26
 * @version: 1.0
 */
@Schema(title = "查询用户已上传分片列表的参数实体")
@Data
public class QueryUploadedChunksBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2527797360829109468L;

    @Schema(title = "文件的唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件的唯一标识不能为空")
    private String identifier;
}