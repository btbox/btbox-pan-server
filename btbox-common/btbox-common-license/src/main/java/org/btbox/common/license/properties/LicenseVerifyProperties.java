package org.btbox.common.license.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/11/13 15:05
 * @version: 1.0
 */
@Data
@ConfigurationProperties(prefix = "license")
public class LicenseVerifyProperties {

    /**
     * 是否启用
     */
    private Boolean enable;

    /**
     * 证书subject
     */
    private String subject;

    /**
     * 公钥别称
     */
    private String publicAlias;

    /**
     * 访问公钥库的密码
     */
    private String storePass;

    /**
     * 证书生成路径
     */
    private String licensePath;

    /**
     * 加密文件
     */
    private String licenseTxt;

    /**
     * 密钥库存储路径
     */
    private String publicKeysStorePath;

    /**
     * 加密密码
     */
    private String licenseTxtPwd;

    /**
     * 加密偏移量
     */
    private String licenseTxtLv;

}