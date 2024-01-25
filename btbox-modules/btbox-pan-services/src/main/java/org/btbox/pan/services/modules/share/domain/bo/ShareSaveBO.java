package org.btbox.pan.services.modules.share.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/25 11:35
 * @version: 1.0
 */
@Schema(title = "保存至我的网盘参数实体对象")
@Data
public class ShareSaveBO {

    @Schema(title = "要转存的文件ID集合，多个使用公用分隔符拼接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要保存的文件")
    private String fileIds;

    @Schema(title = "要转存到的文件夹ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要保存到的文件夹")
    private String targetParentId;

}