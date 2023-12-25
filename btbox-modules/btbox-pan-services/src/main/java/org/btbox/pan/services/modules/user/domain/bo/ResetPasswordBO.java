package org.btbox.pan.services.modules.user.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.btbox.pan.services.modules.user.domain.context.ResetPasswordContext;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/25 15:58
 * @version: 1.0
 */
@Schema(title = "用户忘记密码-重置用户密码参数")
@AutoMapper(target = ResetPasswordContext.class)
@Data
public class ResetPasswordBO implements Serializable {
    @Serial
    private static final long serialVersionUID = 8419027491665645420L;

    @Schema(title = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;

    @Schema(title = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Length(min = 8, max = 16, message = "请输入8-16位的密码")
    private String password;

    @Schema(title = "提交重置密码的token", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "提交重置密码的token不能为空")
    private String token;
}