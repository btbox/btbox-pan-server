package org.btbox.pan.services.modules.user.service;

import org.btbox.pan.services.modules.user.domain.context.UserLoginContext;
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/13 16:51
 * @version: 1.0
 */
public interface UserService {

    /**
     * 注册用户
     * @author: BT-BOX(HJH)
     * @param userRegisterContext
     * @version: 1.0
     * @createDate: 2023/12/14 10:22
     * @return: java.lang.Long
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录业务
     * @author: BT-BOX(HJH)
     * @param userLoginContext
     * @version: 1.0
     * @createDate: 2023/12/15 10:19
     * @return: java.lang.String
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 用户登出
     * @author: BT-BOX(HJH)
     * @param
     * @version: 1.0
     * @createDate: 2023/12/25 9:10
     * @return: void
     */
    void exit();
}
