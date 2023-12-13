package org.btbox.common.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * MP注入处理器
 *
 * @author Lion Li
 * @date 2021/4/25
 */
@Slf4j
public class InjectionMetaObjectHandler implements MetaObjectHandler {

    public static final long DEFAULT_USER_ID = 1L;

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = ObjectUtil.isNotNull(baseEntity.getCreateTime())
                    ? baseEntity.getCreateTime() : new Date();
                baseEntity.setCreateTime(current);
                baseEntity.setUpdateTime(current);
                // 当前已登录 且 创建人为空 则填充
                baseEntity.setCreateBy(1L);
                // 当前已登录 且 更新人为空 则填充
                baseEntity.setUpdateBy(1L);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject) && metaObject.getOriginalObject() instanceof BaseEntity baseEntity) {
                Date current = new Date();
                // 更新时间填充(不管为不为空)
                baseEntity.setUpdateTime(current);
                // 当前已登录 更新人填充(不管为不为空)
                baseEntity.setUpdateBy(DEFAULT_USER_ID);
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

}
