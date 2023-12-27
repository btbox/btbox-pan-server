package org.btbox.common.core.event.file;

import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @description: 文件删除事件
 * @author: BT-BOX
 * @createDate: 2023/12/27 9:41
 * @version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class DeleteFileEvent extends ApplicationEvent {

    private List<Long> fileIdList;

    public DeleteFileEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }
}