package org.btbox.pan.services.modules.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.pan.services.modules.user.domain.context.UserLoginContext;
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
// @Transactional
public class UserTest {

    @Autowired
    private UserService userService;

    private final static String USERNAME = "btbox1";

    private final static String PASSWORD = "12345678";

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

    /**
     * 测试登录成功
     */
    @Test
    public void loginSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String accessToken = userService.login(userLoginContext);
        System.out.println("token = " + accessToken);
        Assert.isTrue(StringUtils.isNoneBlank(accessToken));
    }

    /**
     * 测试登录失败：用户名不正确
     */
    @Test
    public void wrongUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setUsername("tt");
        userService.login(userLoginContext);
    }

    /**
     * 测试登录失败：密码不正确
     */
    @Test
    public void wrongPassword() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setPassword("333");
        userService.login(userLoginContext);
    }

    /**
     * 登出测试
     */
    @Test
    public void exitSuccess() {
        // UserRegisterContext context = createUserRegisterContext();
        // Long register = userService.register(context);
        // Assert.isTrue(register > 0L);
        // 1735499970887966722
        StpUtil.login("1735499970887966722");
        // StpUtil.logout(register);
    }


    /**************************** private ****************************/

    /**
     * 构建用户登录实体
     * @return
     */
    private UserLoginContext createUserLoginContext() {
        UserLoginContext context = new UserLoginContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        return context;
    }

    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion("question");
        context.setAnswer("answer");
        return context;
    }

}