package org.btbox.pan.services.modules.share.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/17 9:34
 * @version: 1.0
 */
@Schema(title = "分享者信息返回实体对象")
@Data
public class ShareUserInfoVO {

    @Schema(title = "分享者的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long userId;

    @Schema(title = "分享者的名称")
    private String username;

}