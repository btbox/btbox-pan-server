package org.btbox.pan.services.modules.user.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.btbox.pan.services.modules.user.domain.context.CheckUsernameContext;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/25 11:22
 * @version: 1.0
 */
@Schema(title = "用户忘记密码-校验用户名参数")
@Data
@AutoMapper(target = CheckUsernameContext.class)
public class CheckUsernameBO implements Serializable {
    @Serial
    private static final long serialVersionUID = -5045955636061983162L;

    @Schema(title = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;

}