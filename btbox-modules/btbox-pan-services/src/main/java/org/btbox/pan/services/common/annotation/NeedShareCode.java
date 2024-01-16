package org.btbox.pan.services.common.annotation;

import java.lang.annotation.*;

/**
 * @description: 该注解主要英雄需要分享码校验的接口
 * @author: BT-BOX
 * @createDate: 2024/1/16 17:22
 * @version: 1.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NeedShareCode {
}