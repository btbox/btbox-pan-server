package org.btbox.pan.services.common.listenner.log;

import lombok.RequiredArgsConstructor;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.common.event.log.ErrorLogEvent;
import org.btbox.pan.services.modules.log.domain.entity.PanErrorLog;
import org.btbox.pan.services.modules.log.service.PanErrorLogService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @description: 系统错误日志监听器
 * @author: BT-BOX
 * @createDate: 2023/12/27 17:15
 * @version: 1.0
 */
@Component
@RequiredArgsConstructor
public class ErrorLogEventListener {

    private final PanErrorLogService panErrorLogService;

    /**
     * 监听系统错误日志事件，并奥村到数据库中
     */
    @EventListener(ErrorLogEvent.class)
    public void saveErrorLog(ErrorLogEvent event) {
        PanErrorLog record = new PanErrorLog();
        record.setId(IdUtil.get());
        record.setLogContent(event.getErrorMsg());
        record.setLogStatus(0);
        record.setCreateUser(event.getUserId());
        record.setUpdateUser(event.getUserId());
        panErrorLogService.save(record);
    }

}