package org.btbox.pan.services.modules.recycle.service.impl;

import lombok.RequiredArgsConstructor;
import org.btbox.common.core.enums.DelFlagEnum;
import org.btbox.pan.services.modules.file.domain.context.QueryFileListContext;
import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.file.service.UserFileService;
import org.btbox.pan.services.modules.recycle.domain.context.QueryRecycleFileListContext;
import org.btbox.pan.services.modules.recycle.service.RecycleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/4 16:11
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class RecycleServiceImpl implements RecycleService {

    private final UserFileService userFileService;

    /**
     * 查询用户的回收站文件列表
     * @param context
     * @return
     */
    @Override
    public List<UserFileVO> recycles(QueryRecycleFileListContext context) {
        QueryFileListContext queryFileListContext = new QueryFileListContext();
        queryFileListContext.setUserId(context.getUserId());
        queryFileListContext.setDelFlag(DelFlagEnum.YES.getCode());
        return userFileService.getFileList(queryFileListContext);
    }
}