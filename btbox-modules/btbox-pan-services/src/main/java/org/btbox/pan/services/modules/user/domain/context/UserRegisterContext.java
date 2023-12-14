package org.btbox.pan.services.modules.user.domain.context;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;

import java.io.Serial;
import java.io.Serializable;
/**
 * 用户注册业务的上下文实体对象
 */
@Data
@AutoMapper(target = BtboxPanUser.class)
public class UserRegisterContext implements Serializable {

    @Serial
    private static final long serialVersionUID = -4835860208501507531L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;

    /**
     * 用户实体对象
     */
    private BtboxPanUser entity;

}
