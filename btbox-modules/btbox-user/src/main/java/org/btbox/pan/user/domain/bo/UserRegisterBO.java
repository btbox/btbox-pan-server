package org.btbox.pan.user.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.btbox.pan.user.domain.entity.BtboxPanUser;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/13 16:18
 * @version: 1.0
 */
@Data
@AutoMapper(target = BtboxPanUser.class)
public class UserRegisterBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -9106305913458592523L;

    @Schema(name = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;

    @Schema(name = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密码不能为空")
    @Length(min = 8, max = 16, message = "请输入8-16位的密码")
    private String password;

    @Schema(name = "密码问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密保问题不能为空")
    @Length(max = 100, message = "密保问题不能超过100个字符")
    private String question;

    @Schema(name = "密码答案", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密保答案不能为空")
    @Length(max = 100, message = "密保答案不能超过100个字符")
    private String answer;

}