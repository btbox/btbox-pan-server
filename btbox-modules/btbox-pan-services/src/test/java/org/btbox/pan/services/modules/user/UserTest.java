package org.btbox.pan.services.modules.user;

import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * @description: 用户模块单元测试类
 * @author: BT-BOX
 * @createDate: 2023/12/14 16:38
 * @version: 1.0
 */
@SpringBootTest
@Transactional
public class UserTest {

    @Autowired
    private UserService userService;


}