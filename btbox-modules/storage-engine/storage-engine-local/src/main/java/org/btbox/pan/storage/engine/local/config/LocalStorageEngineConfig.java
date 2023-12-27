package org.btbox.pan.storage.engine.local.config;

import lombok.Data;
import org.btbox.common.core.utils.file.FileUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/12/27 16:46
 * @version: 1.0
 */
@Component
@ConfigurationProperties(prefix = "btbox-pan.storage.engine.local")
@Data
public class LocalStorageEngineConfig {

    private String rootFilePath = FileUtils.generateDefaultStoreFileRealPath();

}