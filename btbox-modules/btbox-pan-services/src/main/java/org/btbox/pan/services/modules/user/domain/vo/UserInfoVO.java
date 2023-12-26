package org.btbox.pan.services.modules.user.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.btbox.common.json.utils.IdEncryptSerializer;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/25 17:43
 * @version: 1.0
 */
@Schema(title = "用户基本信息实体")
@Data
public class UserInfoVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 4447037630265519109L;

    @Schema(title = "用户名称")
    private String username;

    @JsonSerialize(using = IdEncryptSerializer.class)
    @Schema(title = "用户根目录ID")
    private Long rootFileId;

    @Schema(title = "用户根目录名称")
    private String rootFilename;

}