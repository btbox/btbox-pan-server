package org.btbox.pan.services.modules.share.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 11:07
 * @version: 1.0
 */
@Schema(title = "创建分享链接的参数实体对象")
@Data
public class CreateShareUrlBO {

    @Schema(title = "分享的名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享的名称不能为空")
    private String shareName;

    @Schema(title = "分享的类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分享的类型不能为空")
    private Integer shareType;

    @Schema(title = "分享的日期类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分享的日期类型不能为空")
    private Integer shareDayType;

    @Schema(title = "分享的文件ID集合,多个使用公用的分隔符拼接", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享的文件ID集合不能为空")
    private String shareFileIds;

}