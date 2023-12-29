package org.btbox.pan.services.modules.file.domain.context;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件下载的上下文实体对象
 * @author: BT-BOX
 * @createDate: 2023/12/29 11:05
 * @version: 1.0
 */
@Data
public class FileDownloadContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 4670192829304252164L;

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 请求响应对象
     */
    private HttpServletResponse response;

    /**
     * 当前登录用户ID
     */
    private Long userId;
}