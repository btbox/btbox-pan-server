package org.btbox.pan.services.modules.user.domain.context;

import lombok.Data;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/15 10:17
 * @version: 1.0
 */
@Data
public class UserLoginContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 6769667638786658841L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户实例对象
     */
    private BtboxPanUser entity;

    /**
     * 登录成功后的凭证
     */
    private String accessToken;
}