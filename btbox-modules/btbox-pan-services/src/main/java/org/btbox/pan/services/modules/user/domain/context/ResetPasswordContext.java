package org.btbox.pan.services.modules.user.domain.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/25 15:58
 * @version: 1.0
 */
@Data
public class ResetPasswordContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 8419027491665645420L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 新密码
     */
    private String password;

    /**
     * 提交重置密码的token
     */
    private String token;
}