package org.btbox.pan.services.modules.user.domain.context;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/25 17:05
 * @version: 1.0
 */
@Data
public class ChangePasswordContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 3970517575676973256L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 用户对象
     */
    private BtboxPanUser entity;

}