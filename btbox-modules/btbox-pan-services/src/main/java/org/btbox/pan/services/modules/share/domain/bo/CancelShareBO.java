package org.btbox.pan.services.modules.share.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 15:34
 * @version: 1.0
 */
@Schema(title = "取消分享参数实体对象")
@Data
public class CancelShareBO {

    @Schema(title = "要取消的分享ID的集合，多个使用公用的分割符拼接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "请选择要取消的分享")
    private String shareIds;

}