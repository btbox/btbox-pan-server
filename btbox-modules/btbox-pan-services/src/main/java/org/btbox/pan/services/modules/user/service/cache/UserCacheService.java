package org.btbox.pan.services.modules.user.service.cache;

import lombok.RequiredArgsConstructor;
import org.btbox.common.core.constant.CacheConstants;
import org.btbox.pan.services.common.cache.AnnotationCacheService;
import org.btbox.pan.services.modules.user.domain.entity.BtboxPanUser;
import org.btbox.pan.services.modules.user.repository.mapper.BtboxPanUserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 用户模块缓存业务处理类
 */
@Component(value = "userAnnotationCacheService")
@RequiredArgsConstructor
public class UserCacheService implements AnnotationCacheService<BtboxPanUser> {

    private final BtboxPanUserMapper mapper;

    /**
     * 根据ID查询实体
     *
     * @param id
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.BTBOX_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator", sync = true)
    @Override
    public BtboxPanUser getById(Serializable id) {
        return mapper.selectById(id);
    }

    /**
     * 根据ID来更新缓存信息
     *
     * @param id
     * @param entity
     * @return
     */
    @CachePut(cacheNames = CacheConstants.BTBOX_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean updateById(Serializable id, BtboxPanUser entity) {
        return mapper.updateById(entity) == 1;
    }

    /**
     * 根据ID来删除缓存信息
     *
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = CacheConstants.BTBOX_PAN_CACHE_NAME, keyGenerator = "userIdKeyGenerator")
    @Override
    public boolean removeById(Serializable id) {
        return mapper.deleteById(id) == 1;
    }
}
