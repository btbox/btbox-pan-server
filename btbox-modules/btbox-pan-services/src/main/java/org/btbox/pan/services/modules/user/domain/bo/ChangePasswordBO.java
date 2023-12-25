package org.btbox.pan.services.modules.user.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.btbox.pan.services.modules.user.domain.context.CheckAnswerContext;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 用户在线修改密码参数
 * @author: BT-BOX
 * @createDate: 2023/12/25 17:22
 * @version: 1.0
 */
@Schema(title = "用户在线修改密码参数")
@Data
@AutoMapper(target = CheckAnswerContext.class)
public class ChangePasswordBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3852160050318258308L;

    @Schema(title = "旧密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "旧密码不能为空")
    @Length(min = 8, max = 16, message = "请输入8-16位的密码")
    private String oldPassword;

    @Schema(title = "新密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "新密码不能为空")
    @Length(min = 8, max = 16, message = "请输入8-16位的密码")
    private String newPassword;

}