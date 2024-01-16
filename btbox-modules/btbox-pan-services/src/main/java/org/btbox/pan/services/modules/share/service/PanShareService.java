package org.btbox.pan.services.modules.share.service;

import org.btbox.pan.services.modules.share.convert.CancelShareContext;
import org.btbox.pan.services.modules.share.domain.context.CreateShareUrlContext;
import org.btbox.pan.services.modules.share.domain.context.QueryShareListContext;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import com.baomidou.mybatisplus.extension.service.IService;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;

import java.util.List;

/**
 * @description: 
 * @author: BT-BOX
 * @createDate: 2024/1/16 10:38
 * @version: 1.0
*/
public interface PanShareService extends IService<PanShare>{

    /**
     * 创建分享链接
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/16 11:23
     * @return: org.btbox.pan.services.modules.share.domain.vo.RPanShareUrlVO
     */
    PanShareUrlVO create(CreateShareUrlContext context);

    /**
     * 查询用户的分享列表
     * @param context
     * @return
     */
    List<PanShareUrlListVO> getShares(QueryShareListContext context);

    /**
     * 取消分享链接
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/16 16:11
     * @return: void
     */
    void cancelShare(CancelShareContext context);
}
