package org.btbox.pan.services.modules.share.service;

import org.btbox.pan.services.modules.file.domain.vo.UserFileVO;
import org.btbox.pan.services.modules.share.convert.CancelShareContext;
import org.btbox.pan.services.modules.share.convert.ShareSaveContext;
import org.btbox.pan.services.modules.share.domain.context.*;
import org.btbox.pan.services.modules.share.domain.entity.PanShare;
import com.baomidou.mybatisplus.extension.service.IService;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlListVO;
import org.btbox.pan.services.modules.share.domain.vo.PanShareUrlVO;
import org.btbox.pan.services.modules.share.domain.vo.ShareDetailVO;
import org.btbox.pan.services.modules.share.domain.vo.ShareSimpleDetailVO;

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

    /**
     * 校验分享码
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/16 16:43
     * @return: java.lang.String
     */
    String checkShareCode(CheckShareCodeContext context);

    /**
     * 查询分享详情信息
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/17 9:42
     * @return: org.btbox.pan.services.modules.share.domain.vo.ShareDetailVO
     */
    ShareDetailVO detail(QueryShareDetailContext context);

    /**
     * 查询分享的简单详情
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/17 11:21
     * @return: org.btbox.pan.services.modules.share.domain.vo.ShareSimpleDetailVO
     */
    ShareSimpleDetailVO simpleDetail(QueryShareSimpleDetailContext context);

    /**
     * 获取下一级的文件列表
     * @author: BT-BOX(HJH)
     * @param context
     * @version: 1.0
     * @createDate: 2024/1/25 10:29
     * @return: java.util.List<org.btbox.pan.services.modules.file.domain.vo.UserFileVO>
     */
    List<UserFileVO> fileList(QueryChildFileListContext context);

    /**
     * 转存至我的网盘
     * @param context
     */
    void saveFiles(ShareSaveContext context);

    /**
     * 分享文件的下载
     * @param context
     */
    void download(ShareFileDownloadContext context);

    /**
     * 刷新受影响的对应分享的状态
     * @param allAvailableFileIdList
     */
    void refreshShareStatus(List<Long> allAvailableFileIdList);
}
