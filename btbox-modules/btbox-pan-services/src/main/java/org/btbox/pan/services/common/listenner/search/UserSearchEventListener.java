package org.btbox.pan.services.common.listenner.search;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.btbox.common.core.utils.IdUtil;
import org.btbox.pan.services.common.event.search.UserSearchEvent;
import org.btbox.pan.services.modules.user.domain.entity.PanUserSearchHistory;
import org.btbox.pan.services.modules.user.service.PanUserSearchHistoryService;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Date;

/**
 * @description: 用户搜索事件监听器
 * @author: BT-BOX
 * @createDate: 2024/1/3 11:23
 * @version: 1.0
 */
@Component
@RequiredArgsConstructor
public class UserSearchEventListener {

    private final PanUserSearchHistoryService panUserSearchHistoryService;
    
    /**
     * 监听用户搜索事件，将其保存到用户的搜索历史记录中
     */
    @EventListener(classes = UserSearchEvent.class)
    @Async(value = "eventListenerTaskExecutor")
    public void saveSearchHistory(UserSearchEvent event) {
        PanUserSearchHistory record = new PanUserSearchHistory();
        record.setId(IdUtil.get());
        record.setUserId(event.getUserId());
        record.setSearchContent(event.getKeyword());
        // record.setCreateTime(new Date());
        // record.setUpdateTime(new Date());
        try {
            panUserSearchHistoryService.save(record);
        } catch (DuplicateKeyException e) {
            LambdaUpdateWrapper<PanUserSearchHistory> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(PanUserSearchHistory::getUserId, event.getUserId());
            updateWrapper.eq(PanUserSearchHistory::getSearchContent, event.getKeyword());
            updateWrapper.set(PanUserSearchHistory::getUpdateTime, new Date());
            panUserSearchHistoryService.update(updateWrapper);
        }
    }
}