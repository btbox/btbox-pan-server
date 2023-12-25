package org.btbox.pan.services.modules.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.Assert;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.pan.services.modules.user.domain.context.*;
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

    public static final String QUESTION = "question";
    public static final String ANSWER = "answer";
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
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
        StpUtil.login(register);
        StpUtil.logout(register);
    }

    /**
     * 校验用户名称通过
     */
    @Test
    public void checkUsernameSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername(USERNAME);
        String question = userService.checkUsername(checkUsernameContext);
        Assert.isTrue(StringUtils.isNoneBlank(question));
    }

    /**
     * 校验用户名称失败-没有查询到该用户
     */
    @Test
    public void checkUsernameNotExist() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername("USERNAME");
        String question = userService.checkUsername(checkUsernameContext);
    }

    /**
     * 校验密保问题答案
     */
    @Test
    public void checkAnswerSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER);

        String token = userService.checkAnswer(checkAnswerContext);
        Assert.isTrue(StringUtils.isNoneBlank(token));
    }

    /**
     * 校验密保问题答案失败-密保问题答案不正确
     */
    @Test
    public void checkAnswerError() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer("ANSWER");

        String token = userService.checkAnswer(checkAnswerContext);
    }

    /**
     * 重置用户密码成功
     */
    @Test
    public void resetPasswordSuccess() {
        // 注册用户
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);

        // 获取token
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER);
        String token = userService.checkAnswer(checkAnswerContext);

        // 重置密码
        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setUsername(USERNAME);
        resetPasswordContext.setPassword(PASSWORD + "_change");
        resetPasswordContext.setToken(token);
        userService.resetPassword(resetPasswordContext);

    }

    /**
     * 重置用户密码错误
     */
    @Test
    public void resetPasswordError() {
        // 注册用户
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register > 0L);

        // 获取token
        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER);
        String token = userService.checkAnswer(checkAnswerContext);

        System.out.println("token = " + token);
        // 重置密码
        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setUsername(USERNAME);
        resetPasswordContext.setPassword(PASSWORD + "_change");
        resetPasswordContext.setToken("change");
        userService.resetPassword(resetPasswordContext);

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
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

}