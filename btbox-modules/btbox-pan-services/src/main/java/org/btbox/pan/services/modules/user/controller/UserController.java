package org.btbox.pan.services.modules.user.controller;

import cn.hutool.core.util.IdUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.pan.services.modules.user.domain.bo.UserRegisterBO;
import org.btbox.pan.services.modules.user.domain.context.UserRegisterContext;
import org.btbox.pan.services.modules.user.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
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

}
