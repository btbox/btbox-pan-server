package org.btbox.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description: 文件删除标识枚举类
 * @author: BT-BOX
 * @createDate: 2023/12/14 15:57
 * @version: 1.0
*/
@Getter
@AllArgsConstructor
public enum DelFlagEnum {

    NO(0),
    YES(1),

    ;
    private Integer code;

}
