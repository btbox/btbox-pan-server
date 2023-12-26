package org.btbox.common.satoken.utils;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaStorage;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.btbox.common.core.constant.UserConstants;
import org.btbox.common.core.domain.model.LoginUser;
import org.btbox.common.core.enums.UserType;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/26 16:03
 * @version: 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String LOGIN_USER_KEY = "loginUser";

    public static final String USER_KEY = "userId";


    public static void login(LoginUser loginUser, SaLoginModel model) {

        SaStorage storage = SaHolder.getStorage();
        storage.set(LOGIN_USER_KEY, loginUser);
        storage.set(USER_KEY, loginUser.getUserId());
        model = ObjectUtil.defaultIfNull(model, new SaLoginModel());
        StpUtil.login(
                loginUser.getLoginId(),
                model.setExtra(USER_KEY, loginUser.getUserId())
        );
        StpUtil.getSession().set(LOGIN_USER_KEY, loginUser);

    }

    /**
     * 获取用户(多级缓存)
     */
    public static LoginUser getLoginUser() {
        LoginUser loginUser = (LoginUser) SaHolder.getStorage().get(LOGIN_USER_KEY);
        if (loginUser != null) {
            return loginUser;
        }
        SaSession session = StpUtil.getSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        loginUser = (LoginUser) session.get(LOGIN_USER_KEY);
        SaHolder.getStorage().set(LOGIN_USER_KEY, loginUser);
        return loginUser;
    }

    /**
     * 获取用户基于token
     */
    public static LoginUser getLoginUser(String token) {
        Object loginId = StpUtil.getLoginIdByToken(token);
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return Convert.toLong(getExtra(USER_KEY));
    }

    public static String getUserIdAsString() {
        return String.valueOf(getExtra(USER_KEY));
    }

    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return getLoginUser().getUsername();
    }

    // /**
    //  * 获取用户类型
    //  */
    // public static UserType getUserType() {
    //     String loginType = StpUtil.getLoginIdAsString();
    //     return UserType.getUserType(loginType);
    // }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isSuperAdmin(Long userId) {
        return UserConstants.SUPER_ADMIN_ID.equals(userId);
    }

    public static boolean isSuperAdmin() {
        return isSuperAdmin(getUserId());
    }



    private static Object getExtra(String key) {
        Object obj;
        try {
            obj = SaHolder.getStorage().get(key);
            if (ObjectUtil.isNull(obj)) {
                obj = StpUtil.getExtra(key);
                SaHolder.getStorage().set(key, obj);
            }
        } catch (Exception e) {
            return null;
        }
        return obj;
    }

}