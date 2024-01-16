package org.btbox.pan.services.common.config;

import lombok.Data;
import org.btbox.common.core.constant.BtboxConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/28 15:28
 * @version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "btbox-pan.server")
@Data
public class PanServerConfig {

    @Value("${server.port}")
    private Integer serverPort;

    /**
     * 文件分片的过期天数
     */
    private Integer chunkFileExpirationDays = BtboxConstants.ONE_INT;

    private String sharePrefix = "http://127.0.0.1:" + serverPort + "/share/";
}