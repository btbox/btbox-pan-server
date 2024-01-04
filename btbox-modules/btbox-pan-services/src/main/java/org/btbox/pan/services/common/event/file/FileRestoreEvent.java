package org.btbox.pan.services.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @description: 文件还原事件
 * @author: BT-BOX
 * @createDate: 2024/1/4 17:47
 * @version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FileRestoreEvent extends ApplicationEvent {

    /**
     * 被成功还原的文件ID集合
     */
    private List<Long> fileIdList;

    public FileRestoreEvent(Object source, List<Long> fileIdList) {
        super(source);
        this.fileIdList = fileIdList;
    }
}