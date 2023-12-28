package org.btbox.pan.services.modules.file.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:05
 * @version: 1.0
 */
@Schema(title = "文件分片上传的响应实体")
@Data
public class FileChunkUploadVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -9199517555625708410L;

    @Schema(title = "是否需要合并文件 0 不需要 1 需要")
    private Integer mergeFlag;
}