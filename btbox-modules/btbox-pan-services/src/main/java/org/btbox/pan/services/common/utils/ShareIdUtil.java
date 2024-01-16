package org.btbox.pan.services.common.utils;

import cn.hutool.core.util.ObjectUtil;
import org.btbox.common.core.constant.BtboxConstants;

/**
 * @description: 分享ID存储类
 * @author: BT-BOX
 * @createDate: 2024/1/16 17:20
 * @version: 1.0
 */
public class ShareIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void set(Long shareId) {
        threadLocal.set(shareId);
    }

    /**
     * 获取当前线程的分享ID
     * @return
     */
    public static Long get() {
        Long shareId = threadLocal.get();
        if (ObjectUtil.isNull(shareId)) {
            return BtboxConstants.ZERO_LONG;
        }
        return shareId;
    }

}