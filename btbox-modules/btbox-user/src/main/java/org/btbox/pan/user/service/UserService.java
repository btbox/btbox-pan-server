package org.btbox.pan.user.service;

import org.btbox.pan.user.domain.bo.UserRegisterBO;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/13 16:51
 * @version: 1.0
 */
public interface UserService {

    Long register(UserRegisterBO userRegisterBO);

}
