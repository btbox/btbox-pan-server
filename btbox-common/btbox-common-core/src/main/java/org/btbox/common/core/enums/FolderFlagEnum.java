package org.btbox.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 文件夹表示枚举类
 * @author: BT-BOX
 * @createDate: 2023/12/14 15:37
 * @version: 1.0
 */
@Getter
@AllArgsConstructor
public enum FolderFlagEnum {

    /**
     * 不是文件夹
     */
    NO(0),

    /**
     * 是文件夹
     */
    YES(1),

    ;

    private final Integer code;

}
