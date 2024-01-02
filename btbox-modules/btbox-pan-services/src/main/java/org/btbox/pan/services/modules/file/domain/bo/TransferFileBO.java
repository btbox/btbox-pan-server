package org.btbox.pan.services.modules.file.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件转移参数实体
 * @author: BT-BOX
 * @createDate: 2024/1/2 11:03
 * @version: 1.0
 */
@Schema(title = "文件转移参数实体")
@Data
public class TransferFileBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8255306793157742387L;

    @Schema(title = "要转移的文件ID集合，多个使用公用分隔符隔开", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "要转移的文件ID集合")
    private String fileIds;

    @Schema(title = "要转移的目标文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "要转移的目标文件夹ID不能为空")
    private String targetParentId;
}