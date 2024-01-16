package org.btbox.pan.services.modules.share.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 16:39
 * @version: 1.0
 */
@Schema(title = "校验分享码参数实体对象")
@Data
public class CheckShareCodeBO {

    @Schema(title = "分享的ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享的ID不能为空")
    private String shareId;

    @Schema(title = "分享的分享码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享的分享码不能为空")
    private String shareCode;

}