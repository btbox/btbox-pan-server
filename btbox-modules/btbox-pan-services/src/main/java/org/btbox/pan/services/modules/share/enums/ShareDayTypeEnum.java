package org.btbox.pan.services.modules.share.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.btbox.common.core.constant.BtboxConstants;

/**
 * @description: 分享日期枚举类
 * @author: BT-BOX
 * @createDate: 2024/1/16 9:29
 * @version: 1.0
 */
@AllArgsConstructor
@Getter
public enum ShareDayTypeEnum {

    PERMANENT_VALIDITY(0, 0, "永久有效"),

    SEVEN_DAYS_VALIDITY(1, 7, "七天有效"),

    THIRTY_DAYS_VALIDITY(2, 30, "三四天有效")

    ;

    private final Integer code;

    private final Integer days;

    private final String desc;

    public static Integer getShareDayByCode(Integer code) {
        if (ObjectUtil.isNull(code)) {
            return BtboxConstants.MINUS_ONE_INT;
        }
        for (ShareDayTypeEnum value : values()) {
            if (ObjectUtil.equals(value.getCode(), code)) {
                return value.getDays();
            }
        }
        return BtboxConstants.MINUS_ONE_INT;
    }

}