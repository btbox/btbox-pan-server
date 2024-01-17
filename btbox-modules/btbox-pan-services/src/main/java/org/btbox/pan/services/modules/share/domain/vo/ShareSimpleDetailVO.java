package org.btbox.pan.services.modules.share.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/17 11:17
 * @version: 1.0
 */
@Schema(title = "查询分享简单详情返回实体对象")
@Data
public class ShareSimpleDetailVO {

    /**
     *
     */
    @Schema(title = "分享ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long shareId;

    @Schema(title = "分享名称")
    private String shareName;

    @Schema(title = "分享者信息")
    private ShareUserInfoVO shareUserInfoVO;

}