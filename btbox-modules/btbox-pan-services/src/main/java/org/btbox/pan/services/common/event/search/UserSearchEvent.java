package org.btbox.pan.services.common.event.search;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/3 11:19
 * @version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserSearchEvent extends ApplicationEvent {

    private String keyword;

    private Long userId;

    public UserSearchEvent(Object source, String keyword, Long userId) {
        super(source);
        this.keyword = keyword;
        this.userId = userId;
    }
}