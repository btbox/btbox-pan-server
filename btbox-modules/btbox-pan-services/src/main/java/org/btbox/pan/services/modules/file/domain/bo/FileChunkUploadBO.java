package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件分片上传参数实体
 * @author: BT-BOX
 * @createDate: 2023/12/28 14:57
 * @version: 1.0
 */
@Schema(title = "文件分片上传参数实体")
@Data
public class FileChunkUploadBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4944059976818583913L;

    @Schema(title = "文件名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名称不能为空")
    private String filename;

    @Schema(title = "文件唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文件名称不能为空")
    private String identifier;

    @Schema(title = "总体的分片数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总体的分片数不能为空")
    private Integer totalChunks;

    @Schema(title = "当前分片的下标", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "当前分片的下标不能为空")
    private Long currentChunkSize;

    @Schema(title = "文件总大小", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文件总大小不能为空")
    private Long totalSize;

    @Schema(title = "分片文件实体", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分片文件实体不能为空")
    private MultipartFile file;

}