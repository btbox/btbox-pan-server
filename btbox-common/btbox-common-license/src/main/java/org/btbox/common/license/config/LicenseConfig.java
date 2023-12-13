package org.btbox.common.license.config;

import cn.hutool.crypto.symmetric.AES;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.btbox.common.license.properties.LicenseVerifyProperties;
import org.btbox.common.license.utils.LicenseVerify;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @description:
 * @author: BT-BOX
 * @createDate: 2023/11/13 15:07
 * @version: 1.0
 */
@Slf4j
@AutoConfiguration(after = LicenseVerifyProperties.class)
@EnableConfigurationProperties(LicenseVerifyProperties.class)
@ConditionalOnProperty(name = "license.enable", havingValue = "true")
public class LicenseConfig {

    @Resource
    private LicenseVerifyProperties licenseVerifyProperties;

    @PostConstruct
    public void init() {
        if(licenseVerifyProperties.getEnable() && StringUtils.isNotBlank(licenseVerifyProperties.getLicensePath())){
            log.info("++++++++ 开始安装证书 ++++++++");

            LicenseVerify licenseVerify = new LicenseVerify();

            //安装证书
            licenseVerify.install(licenseVerifyProperties);

            log.info("++++++++ 证书安装结束 ++++++++");
        }
    }
}