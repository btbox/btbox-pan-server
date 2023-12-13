package org.btbox.common.license.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.btbox.common.core.domain.R;
import org.btbox.common.license.properties.LicenseVerifyProperties;
import org.btbox.common.license.utils.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;


@ConditionalOnProperty(name = "license.enable", havingValue = "true")
@RestController
@RequestMapping("/license")
@RequiredArgsConstructor
@Slf4j
public class LicenseCreatorController {

    private final LicenseVerifyProperties licenseVerifyProperties;

    /**
     * 获取服务器硬件信息
     * @param osName 操作系统类型，如果为空则自动判断
     */
    @GetMapping(value = "/get-server-infos")
    public R<LicenseCheckModel> getServerInfos(@RequestParam(value = "osName",required = false) String osName) {
        //操作系统类型
        if(StringUtils.isBlank(osName)){
            osName = System.getProperty("os.name");
        }
        osName = osName.toLowerCase();

        AbstractServerInfos abstractServerInfos = null;

        //根据不同操作系统类型选择不同的数据获取方法
        if (osName.startsWith("windows")) {
            abstractServerInfos = new WindowsServerInfos();
        } else if (osName.startsWith("linux")) {
            abstractServerInfos = new LinuxServerInfos();
        }else{//其他服务器类型
            abstractServerInfos = new LinuxServerInfos();
        }

        return R.ok(abstractServerInfos.getServerInfos());
    }


    /**
     * 生成证书,licenseCheckModel的可以都不填
     * {
     *     "result": "ok",
     *     "msg": {
     *         "subject": "license_demo",
     *         "privateAlias": "privateKey",
     *         "keyPass": "private_password1234",
     *         "storePass": "public_password1234",
     *         "licensePath": "D:/license/license.lic",
     *         "privateKeysStorePath": "D:/license/privateKeys.keystore",
     *         "issuedTime": "2022-04-10 00:00:01",
     *         "expiryTime": "2022-05-31 23:59:59",
     *         "consumerType": "User",
     *         "consumerAmount": 1,
     *         "description": "这是证书描述信息",
     *         "licenseCheckModel": {
     *             "ipAddress": [],
     *             "macAddress": [],
     *             "cpuSerial": "",
     *             "mainBoardSerial": ""
     *         }
     *     }
     * }
     */
    @PostMapping(value = "/generate-license")
    public R<LicenseCreatorParam> generateLicense(@RequestBody LicenseCreatorParam param) {

        if(StringUtils.isBlank(param.getLicensePath())){
            param.setLicensePath(licenseVerifyProperties.getLicensePath());
        }

        LicenseCreator licenseCreator = new LicenseCreator(param);
        boolean result = licenseCreator.generateLicense();

        if (result) {
            LicenseVerify licenseVerify = new LicenseVerify();
            //安装证书
            log.info("++++++++ 开始安装证书 ++++++++");
            licenseVerify.install(licenseVerifyProperties);
            // 校验加密文件 license.txt
            LicenseTxtVerify licenseTxtVerify = new LicenseTxtVerify();
            licenseTxtVerify.licenseCryptoFile(true);
            log.info("++++++++ 证书安装结束 ++++++++");
            return R.ok(param);
        } else {
            return R.fail("证书文件生成失败");
        }
    }

    /**
     * 安装证书
     * @return
     */
    @PostMapping("install-license")
    public R<Void> installLicense() {

        LicenseVerify licenseVerify = new LicenseVerify();

        //安装证书
        licenseVerify.install(licenseVerifyProperties);

        return R.ok();
    }

    @GetMapping("test-license")
    public R<Void> testLicense() {
        // 校验加密文件 license.txt
        LicenseTxtVerify licenseTxtVerify = new LicenseTxtVerify();
        licenseTxtVerify.licenseCryptoFile(false);
        return R.ok();
    }



}
