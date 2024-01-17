package org.btbox.pan.services.modules.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.btbox.pan.services.modules.user.domain.context.*;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.domain.entity.PanUserSearchHistory;
import org.btbox.pan.services.modules.user.domain.vo.UserInfoVO;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/13 16:51
 * @version: 1.0
 */
public interface UserService extends IService<BtboxPanUser> {

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

    /**
     * 校验用户名
     * @author: BT-BOX(HJH)
     * @param
     * @version: 1.0
     * @createDate: 2023/12/25 11:27
     * @return: void
     */
    String checkUsername(CheckUsernameContext checkUsernameContext);

    /**
     * 校验密保答案
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/25 15:09
     * @return: java.lang.String
     */
    String checkAnswer(CheckAnswerContext context);

    /**
     * 重置用户密码
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/25 16:04
     * @return: void
     */
    void resetPassword(ResetPasswordContext context);

    /**
     * 在线修改密码
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2023/12/25 17:08
     * @return: void
     */
    void changePassword(ChangePasswordContext context);

    /**
     * 查询在线用户的基本信息
     * @author: BT-BOX(HJH)
     * @param userId 用户id
     * @version: 1.0
     * @createDate: 2023/12/25 17:48
     * @return: org.btbox.pan.services.modules.user.domain.vo.UserInfoVO
     */
    UserInfoVO info(Long userId);
}
