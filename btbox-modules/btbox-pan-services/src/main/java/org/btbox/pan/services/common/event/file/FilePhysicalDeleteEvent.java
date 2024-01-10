package org.btbox.pan.services.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.btbox.pan.services.modules.file.domain.entity.UserFile;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @description: 文件被物理删除事件
 * @author: BT-BOX
 * @createDate: 2024/1/10 16:32
 * @version: 1.0
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class FilePhysicalDeleteEvent extends ApplicationEvent {

    /**
     * 所有被物理删除的文件实体集合
     */
    private List<UserFile> allRecords;

    public FilePhysicalDeleteEvent(Object source, List<UserFile> allRecords) {
        super(source);
        this.allRecords = allRecords;
    }
}