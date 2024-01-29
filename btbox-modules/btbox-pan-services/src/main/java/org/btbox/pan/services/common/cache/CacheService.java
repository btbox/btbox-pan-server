package org.btbox.pan.services.common.cache;

import java.io.Serializable;

/**
 * @description: 支持业务缓存的顶级Service接口
 * @author: BT-BOX
 * @createDate: 2024/1/10 17:03
 * @version: 1.0
 */
public interface CacheService<V> {

    /**
     * 根据ID查询实体
     * @param id
     * @return
     */
    V getById(Serializable id);

    /**
     * 根据ID来更新缓存信息
     * @param id
     * @return
     */
    boolean updateById(Serializable id, V entity);

    /**
     * 根据ID来删除缓存信息
     * @param id
     * @return
     */
    boolean removeById(Serializable id);

}
