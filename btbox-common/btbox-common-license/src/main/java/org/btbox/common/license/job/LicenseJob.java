package org.btbox.common.license.job;

import ch.qos.logback.core.spi.ContextAware;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.license.exception.LicenseException;
import org.btbox.common.license.utils.LicenseTxtVerify;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @description: 校验证书job
 * @author: BT-BOX
 * @createDate: 2023/11/20 10:35
 * @version: 1.0
 */
@Component
@EnableScheduling
@Slf4j
@ConditionalOnProperty(name = "license.enable", havingValue = "true")
public class LicenseJob implements ApplicationContextAware {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkLicenseTxtFile() {
        try {
            LicenseTxtVerify licenseTxtVerify = new LicenseTxtVerify();
            licenseTxtVerify.licenseCryptoFile(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            applicationContext.close();
        }
    }
}