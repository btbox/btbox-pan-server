package org.btbox.pan.services.modules.user;

import cn.hutool.core.lang.Assert;
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;
import org.btbox.pan.services.modules.user.service.UserService;
import org.junit.jupiter.api.Test;
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

    @Test
    public void testRegister() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
    }

    @Test
    public void testRegisterDuplicateUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
        userService.register(context);
    }

    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername("btbox");
        context.setPassword("123");
        context.setQuestion("question");
        context.setAnswer("answer");
        return context;
    }

}