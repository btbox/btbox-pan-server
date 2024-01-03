package org.btbox.common.mybatis.handler;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.mybatis.core.domain.BaseEntity;
import org.btbox.common.satoken.utils.LoginHelper;

import java.time.LocalDateTime;
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
    public static final String CREATE_TIME = "createTime";
    public static final String UPDATE_TIME = "updateTime";
    public static final String CREATE_USER = "createUser";
    public static final String UPDATE_USER = "updateUser";

    @Override
    public void insertFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject)) {

                Date current = ObjectUtil.isNotNull(metaObject.getValue(CREATE_TIME))
                        ? (Date) metaObject.getValue(CREATE_TIME) : new Date();

                this.strictInsertFill(metaObject, CREATE_TIME, Date.class, current);
                this.strictInsertFill(metaObject, UPDATE_TIME, Date.class, current);


                if (metaObject.hasGetter(CREATE_USER)) {
                    Long userId = ObjectUtil.isNotNull(metaObject.getValue(CREATE_USER))
                            ? (Long) metaObject.getValue(CREATE_USER) : LoginHelper.getUserId();
                    this.strictInsertFill(metaObject, CREATE_USER, Long.class, userId);
                }

                if (metaObject.hasGetter(UPDATE_USER)) {
                    Long userId = ObjectUtil.isNotNull(metaObject.getValue(UPDATE_USER))
                            ? (Long) metaObject.getValue(UPDATE_USER) : LoginHelper.getUserId();
                    this.strictInsertFill(metaObject, UPDATE_USER, Long.class, userId);
                }

            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        try {
            if (ObjectUtil.isNotNull(metaObject)) {

                this.strictInsertFill(metaObject, UPDATE_TIME, Date.class, new Date());

                if (metaObject.hasGetter(UPDATE_USER)) {
                    Long userId = ObjectUtil.isNotNull(metaObject.getValue(UPDATE_USER))
                            ? (Long) metaObject.getValue(UPDATE_USER) : LoginHelper.getUserId();
                    this.strictInsertFill(metaObject, UPDATE_USER, Long.class, userId);
                }
            }
        } catch (Exception e) {
            throw new ServiceException("自动注入异常 => " + e.getMessage(), HttpStatus.HTTP_UNAUTHORIZED);
        }
    }

}
