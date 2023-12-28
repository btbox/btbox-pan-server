package org.btbox.pan.services.modules.file.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 16:24
 * @version: 1.0
 */
@Schema(title = "查询用户已上传的文件分片列表返回实体")
@Data
public class UploadedChunksVO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3551176693906224988L;

    @Schema(title = "已上传的分片编号列表")
    private List<Integer> uploadedChunks;

}