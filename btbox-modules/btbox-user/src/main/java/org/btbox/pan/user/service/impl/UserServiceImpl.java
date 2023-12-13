package org.btbox.pan.user.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.MapstructUtils;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.pan.user.domain.bo.UserRegisterBO;
import org.btbox.pan.user.domain.entity.BtboxPanUser;
import org.btbox.pan.user.repository.mapper.BtboxPanUserMapper;
import org.btbox.pan.user.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/13 16:53
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<BtboxPanUserMapper, BtboxPanUser> implements UserService {

    @Override
    public Long register(UserRegisterBO userRegisterBO) {

        // 转换对象
        BtboxPanUser convert = MapstructUtils.convert(userRegisterBO, BtboxPanUser.class);

        // 注册
        doRegister(convert);


        return convert.getUserId();
    }

    /**
     * 实现注册用户的业务
     * 捕获数据库唯一索引冲突异常，来实现全局用户名称唯一
     * @param convert
     */
    private void doRegister(BtboxPanUser convert) {
        // 查询是否已存在的用户名
        LambdaQueryWrapper<BtboxPanUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BtboxPanUser::getUsername, convert.getUsername());
        BtboxPanUser entity = this.getOne(queryWrapper);

        // 不存在则注册保存
        if (ObjectUtil.isNotEmpty(entity)) {
            try {
                if (!this.save(convert)) {
                    throw new ServiceException(MessageUtils.message("user.register.error"));
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new ServiceException(MessageUtils.message("user.username.has"));
            }
        }
    }
}