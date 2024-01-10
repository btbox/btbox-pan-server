package org.btbox.pan.services.modules.recycle.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/10 16:16
 * @version: 1.0
 */
@Schema(title = "文件删除参数实体")
@Data
public class DeleteBO {

    @Schema(title = "要删除的文件ID集合, 多个使用公用分隔符分隔", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要删除的文件")
    private String fileIds;

}