package org.btbox.pan.services.modules.file.domain.context;

import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @description: 文件预览的上下文对象
 * @author: BT-BOX
 * @createDate: 2023/12/29 15:18
 * @version: 1.0
 */
@Data
public class FilePreviewContext implements Serializable {
    @Serial
    private static final long serialVersionUID = 1199773122805253309L;

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