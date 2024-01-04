package org.btbox.pan.storage.engine.oss.config;

import com.aliyun.oss.OSSClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2024/1/3 15:46
 * @version: 1.0
 */
@Component
@Data
@ConfigurationProperties(prefix = "btbox-pan.storage.engine.oss")
@Slf4j
public class OssStorageEngineConfig {

    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    /**
     * 桶名称
     */
    private String bucketName;

    /**
     * 是否自动创建桶
     */
    private Boolean autoCreateBucket = Boolean.TRUE;

    @Bean(destroyMethod = "shutdown")
    public OSSClient ossClient() {
        if (StringUtils.isAnyBlank(getEndpoint(), getAccessKeyId(), getAccessKeySecret(), getBucketName())) {
            throw new ServiceException("the oss config is missed!");
        }
        return new OSSClient(getEndpoint(), getAccessKeyId(), getAccessKeySecret());
    }

}