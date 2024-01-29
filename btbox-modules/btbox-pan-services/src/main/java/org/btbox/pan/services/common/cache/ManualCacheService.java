package org.btbox.pan.services.common.cache;

import org.springframework.cache.Cache;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @description: 手动缓存处理Service顶级接口
 * @author: BT-BOX
 * @createDate: 2024/1/10 17:03
 * @version: 1.0
 */
public interface ManualCacheService<V> extends CacheService<V> {

    /**
     * 根据ID集合查询实体列表
     * @param ids
     * @return
     */
    List<V> getByIds(Collection<? extends Serializable> ids);


    /**
     * 批量更新实体记录
     * @param entityMap
     * @return
     */
    boolean updateByIds(Map<? extends Serializable, V> entityMap);

    /**
     * 批量删除实体记录
     * @return
     */
    boolean removeByIds(Collection<? extends Serializable> ids);

    /**
     * 获取缓存key的模板信息
     * @return
     */
    String getKeyFormat();

    /**
     * 获取缓存对象实体
     * @return
     */
    Cache getCache();

}
