package org.btbox.pan.services.modules.user.service;

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

}
