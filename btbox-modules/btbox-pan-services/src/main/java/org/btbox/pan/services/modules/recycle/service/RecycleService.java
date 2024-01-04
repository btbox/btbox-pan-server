package org.btbox.pan.services.modules.recycle.service;

import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.recycle.domain.context.QueryRecycleFileListContext;

import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/4 16:10
 * @version: 1.0
 */
public interface RecycleService {

    /**
     * 查询用户的回收站文件列表
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/4 16:15
     * @return: java.util.List<org.btbox.pan.services.modules.file.domain.vo.UserFileVO>
     */
    List<UserFileVO> recycles(QueryRecycleFileListContext context);
}
