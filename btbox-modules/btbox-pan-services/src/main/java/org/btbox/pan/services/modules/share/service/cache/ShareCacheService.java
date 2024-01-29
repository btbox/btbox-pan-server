package org.btbox.pan.services.modules.share.service.cache;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.RequiredArgsConstructor;
import org.btbox.pan.services.common.cache.AbstractManualCacheService;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import org.btbox.pan.services.modules.share.repository.mapper.PanShareMapper;
import org.springframework.stereotype.Component;

/**
 * 手动缓存实现分析业务的查询等操作
 */
@Component(value = "shareManualCacheService")
@RequiredArgsConstructor
public class ShareCacheService extends AbstractManualCacheService<PanShare> {

    private final PanShareMapper panShareMapper;

    @Override
    protected BaseMapper<PanShare> getBaseMapper() {
        return panShareMapper;
    }

    /**
     * 获取缓存key的模板信息
     *
     * @return
     */
    @Override
    public String getKeyFormat() {
        return "SHARE:ID:%s";
    }
}
