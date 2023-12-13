package org.btbox.common.license.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.symmetric.AES;
import de.schlichtherle.license.*;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.DateUtils;
import org.btbox.common.core.utils.SpringUtils;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.common.license.exception.LicenseException;
import org.btbox.common.license.properties.LicenseVerifyProperties;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.prefs.Preferences;

/**
 * License校验类
 */
@Slf4j
public class LicenseVerify {

    /**
     * 安装License证书
     */
    public synchronized LicenseContent install(LicenseVerifyProperties param) {
        LicenseContent result = null;
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 1. 安装证书
        try {
            LicenseManager licenseManager = LicenseManagerHolder.getInstance(initLicenseParam(param));
            licenseManager.uninstall();
            result = licenseManager.install(new File(param.getLicensePath()));
            log.info(MessageFormat.format("证书安装成功，证书有效期：{0} - {1}", format.format(result.getNotBefore()), format.format(result.getNotAfter())));
        } catch (Exception e) {
            log.error("证书安装失败！", e);
        }

        return result;
    }

    /**
     * 校验License证书
     */
    public boolean verify() {
        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 2. 校验证书
        try {
            LicenseContent licenseContent = licenseManager.verify();
//             log.debug(MessageFormat.format("证书校验通过，证书有效期：{0} - {1}",format.format(licenseContent.getNotBefore()),format.format(licenseContent.getNotAfter())));
            return true;
        } catch (Exception e) {
            log.error("证书校验失败！", e);
            return false;
        }
    }

    /**
     * 初始化证书生成参数
     */
    private LicenseParam initLicenseParam(LicenseVerifyProperties param) {
        Preferences preferences = Preferences.userNodeForPackage(LicenseVerify.class);

        CipherParam cipherParam = new DefaultCipherParam(param.getStorePass());

        KeyStoreParam publicStoreParam = new CustomKeyStoreParam(LicenseVerify.class
                , param.getPublicKeysStorePath()
                , param.getPublicAlias()
                , param.getStorePass()
                , null);

        return new DefaultLicenseParam(param.getSubject()
                , preferences
                , publicStoreParam
                , cipherParam);
    }

    /**
     * 获取License证书信息
     */
    public LicenseContent info() {

        LicenseManager licenseManager = LicenseManagerHolder.getInstance(null);

        // 2. 校验证书
        try {
            return licenseManager.verify();
        } catch (Exception e) {
            log.error("证书校验失败！", e);
            return null;
        }
    }

}
