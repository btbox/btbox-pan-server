package org.btbox.pan.services.modules.share.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;

import java.util.Date;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/16 15:13
 * @version: 1.0
 */
@Schema(title = "分享链接列表结果实体对象")
@Data
public class PanShareUrlListVO {

    @Schema(title = "分享的ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @Schema(title = "分享的名称")
    private String shareName;

    @Schema(title = "分享的链接")
    private String shareUrl;

    @Schema(title = "分享的分享码")
    private String shareCode;

    @Schema(title = "分享的状态")
    private Integer shareStatus;

    @Schema(title = "分享的类型")
    private Integer shareType;

    @Schema(title = "分享的过期类型")
    private Integer shareDayType;

    @Schema(title = "分享的过期时间")
    private Date shareEndTime;

    @Schema(title = "分享的创建时间")
    private Date createTime;

}