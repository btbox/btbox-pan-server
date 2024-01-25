package org.btbox.pan.services.modules.share.domain.context;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

/**
 * @description: 分享文件下载上下文实体对象
 * @author: BT-BOX
 * @createDate: 2024/1/25 14:35
 * @version: 1.0
 */
@Data
public class ShareFileDownloadContext {

    /**
     * 要下载的文件ID
     */
    private Long fileId;

    /**
     * 当前登录的用户ID
     */
    private Long userId;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 响应实体
     */
    private HttpServletResponse response;

}