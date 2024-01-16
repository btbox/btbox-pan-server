package org.btbox.pan.services.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 分享类型枚举类
 * @author: BT-BOX
 * @createDate: 2024/1/16 9:29
 * @version: 1.0
 */
@AllArgsConstructor
@Getter
public enum ShareTypeEnum {

    NEED_SHARE_CODE(0, "有提取码"),

    ;

    private final Integer code;

    private final String desc;

}