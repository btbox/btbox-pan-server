package org.btbox.pan.services.common.event.log;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 17:13
 * @version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ErrorLogEvent extends ApplicationEvent {

    /**
     * 错误日志的内容
     */
    private String errorMsg;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    public ErrorLogEvent(Object source, String errorMsg, Long userId) {
        super(source);
        this.errorMsg = errorMsg;
        this.userId = userId;
    }
}