package org.btbox.pan.services.modules.share.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 11:04
 * @version: 1.0
 */
@Schema(title = "创建分享链接的返回实体对象")
@Data
public class RPanShareUrlVO {

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(title = "分享链接ID")
    private Long shareId;

    @Schema(title = "分享链接的名称")
    private String shareName;

    @Schema(title = "分享链接的URL")
    private String shareUrl;

    @Schema(title = "分享链接的分享码")
    private String shareCode;

    @Schema(title = "分享链接的状态")
    private Integer shareStatus;

}