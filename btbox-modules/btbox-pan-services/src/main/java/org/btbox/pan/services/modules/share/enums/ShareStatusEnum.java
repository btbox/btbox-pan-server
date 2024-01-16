package org.btbox.pan.services.modules.share.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 分享状态枚举类
 * @author: BT-BOX
 * @createDate: 2024/1/16 9:29
 * @version: 1.0
 */
@AllArgsConstructor
@Getter
public enum ShareStatusEnum {

    NEED_SHARE_CODE(0, "正常状态"),

    FILE_DELETED(1, "有文件被删除"),

    ;

    private final Integer code;

    private final String desc;

}