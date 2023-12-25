package org.btbox.pan.services.modules.user.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.pan.services.modules.user.domain.bo.*;
import org.btbox.pan.services.modules.user.domain.context.*;
import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("user")
@RestController
@RequiredArgsConstructor
@SaIgnore
@Tag(name = "用户控制器")
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册接口", description = "该接口提供了用户注册的功能，实现了幂等性注册的逻辑，可以放心多并发调用")
    @PostMapping("register")
    public R<Long> register(@Validated @RequestBody UserRegisterBO registerBO) {
        UserRegisterContext convert = MapstructUtils.convert(registerBO, UserRegisterContext.class);
        userService.register(convert);
        return R.ok();
    }

    @Operation(summary = "用户登录接口", description = "该接口提供了用户登录的功能，成功登录后，会返回有时效性的accessToken")
    @PostMapping("login")
    public R<String> register(@Validated @RequestBody UserLoginBO userLoginBO) {
        UserLoginContext convert = MapstructUtils.convert(userLoginBO, UserLoginContext.class);
        userService.login(convert);
        return R.ok();
    }

    @Operation(summary = "用户登出接口", description = "该接口提供了用户登出功能")
    @PostMapping(" ")
    public R<Void> exit() {
        userService.exit();
        return R.ok(MessageUtils.message("user.logout.success"));
    }

    @Operation(summary = "用户忘记密码-校验用户名", description = "该接口提供了用户忘记密码-校验用户名")
    @PostMapping("username/check")
    public R<String> checkUsername(@Validated @RequestBody CheckUsernameBO checkUsernameBO) {
        CheckUsernameContext context = MapstructUtils.convert(checkUsernameBO, CheckUsernameContext.class);
        return R.ok(userService.checkUsername(context));
    }

    @Operation(summary = "用户忘记密码-校验密保答案", description = "该接口提供了用户忘记密码-校验密保答案")
    @PostMapping("answer/check")
    public R<String> checkAnswer(@Validated @RequestBody CheckAnswerBO checkAnswerBO) {
        CheckAnswerContext context = MapstructUtils.convert(checkAnswerBO, CheckAnswerContext.class);
        return R.ok(userService.checkAnswer(context));
    }

    @Operation(summary = "用户忘记密码-重置用户密码", description = "该接口提供了用户忘记密码-重置用户密码")
    @PostMapping("password/reset")
    public R<Void> resetPassword(@Validated @RequestBody ResetPasswordBO resetPasswordBO) {
        ResetPasswordContext context = MapstructUtils.convert(resetPasswordBO, ResetPasswordContext.class);
        userService.resetPassword(context);
        return R.ok();
    }

    @Operation(summary = "用户在线修改密码", description = "该接口提供了用户在线修改密码")
    @PostMapping("password/change")
    public R<Void> changePassword(@Validated @RequestBody ChangePasswordBO changePasswordBO) {
        ChangePasswordContext context = MapstructUtils.convert(changePasswordBO, ChangePasswordContext.class);
        userService.changePassword(context);
        return R.ok();
    }

}
