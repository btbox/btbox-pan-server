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
 * @description: 校验密保答案
 * @author: BT-BOX
 * @createDate: 2023/12/25 11:22
 * @version: 1.0
 */
@Schema(title = "用户忘记密码-校验密保答案参数")
@Data
@AutoMapper(target = CheckAnswerContext.class)
public class CheckAnswerBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3066103722808651124L;

    @Schema(title = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;

    @Schema(title = "密码问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密保问题不能为空")
    @Length(max = 100, message = "密保问题不能超过100个字符")
    private String question;

    @Schema(title = "密码答案", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "密保答案不能为空")
    @Length(max = 100, message = "密保答案不能超过100个字符")
    private String answer;

}