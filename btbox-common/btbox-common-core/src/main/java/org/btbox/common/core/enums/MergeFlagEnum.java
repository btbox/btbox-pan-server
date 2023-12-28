package org.btbox.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:12
 * @version: 1.0
 */
@Getter
@AllArgsConstructor
public enum MergeFlagEnum {

    /**
     * 不需要合并
     */
    NOT_READY(0),

    /**
     * 需要合并
     */
    READY(1)

    ;

    private final Integer code;

}
