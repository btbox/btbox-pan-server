package org.btbox.common.license.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.symmetric.AES;
import de.schlichtherle.license.LicenseContent;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.exception.ServiceException;
import org.btbox.common.core.utils.SpringUtils;
import org.btbox.common.core.utils.StringUtils;
import org.btbox.common.core.utils.file.FileUtils;
import org.btbox.common.license.exception.LicenseException;
import org.btbox.common.license.properties.LicenseVerifyProperties;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.btbox.common.core.utils.DateUtils.LOCALDATETIME_YYYY_MM_DD_HH_MM_SS;

/**
 * @description: licenseTxt文件校验
 * @author: BT-BOX
 * @createDate: 2023/11/20 9:53
 * @version: 1.0
 */
@Slf4j
public class LicenseTxtVerify {

    public static final LicenseVerifyProperties LICENSE_VERIFY_PROPERTIES = SpringUtils.getBean(LicenseVerifyProperties.class);

    /**
     * 加密的AES对象实例
     */
    public static final AES licenseTxtAES = new AES("CBC", "PKCS7Padding",
            // 密钥，可以自定义
            LICENSE_VERIFY_PROPERTIES.getLicenseTxtPwd().getBytes(),
            // iv加盐，按照实际需求添加
            LICENSE_VERIFY_PROPERTIES.getLicenseTxtLv().getBytes());


    /**
     * 校验license.txt
     * @param install 是否是安装证书流程
     */
    public void licenseCryptoFile(boolean install) {

        LicenseVerifyProperties licenseVerifyProperties = SpringUtils.getBean(LicenseVerifyProperties.class);
        String licenseTxtPath = licenseVerifyProperties.getLicenseTxt();

        LicenseVerify licenseVerify = new LicenseVerify();

        // 1. 获取证书
        LicenseContent licenseContent = licenseVerify.info();

        // 获取证书项目名称
        String subject = licenseContent.getSubject();
        // 证书过期时间
        Date notAfter = licenseContent.getNotAfter();

        // 判断加密文件是否存在
        boolean licenseTxtExist = FileUtils.exist(licenseTxtPath);
        // 存在文件则解密文件
        if (licenseTxtExist) {

            existLicenseFileCheck(licenseTxtPath, subject);

        } else {
            // 如果是安装证书流程则创建license.txt文件并且赋予证书结束时间
            if (install) {

                notExistLicenseFileInInstall(licenseTxtPath, notAfter, subject);

            } else {
                throw new LicenseException("license.txt文件不存在,请联系供应商");
            }
        }

    }

    /**
     * 不存在license.txt文件并且是进行安装步骤
     * @param licenseTxtPath
     * @param notAfter
     * @param subject
     */
    private static void notExistLicenseFileInInstall(String licenseTxtPath, Date notAfter, String subject) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;

        try {

            File licenseTxtFile = FileUtils.file(licenseTxtPath);

            // 创建流
            outputStream = new FileOutputStream(licenseTxtFile);
            // 新new OutputStreamWriter对象，记得关闭回收
            outputStreamWriter = IoUtil.getUtf8Writer(outputStream);

            LocalDateTime licenseAfterTime = LocalDateTime.ofInstant(notAfter.toInstant(), ZoneId.systemDefault());

            String content = subject + ";" + licenseAfterTime.format(LOCALDATETIME_YYYY_MM_DD_HH_MM_SS);
            // 加密重写后的内容
            String encryptContent = licenseTxtAES.encryptHex(content);
            int c;
            for (int i = 0; i < encryptContent.length(); i++) {
                c = encryptContent.charAt(i);
                outputStreamWriter.write((char) c);
            }
            IoUtil.flush(outputStreamWriter);

        } catch (IOException ie) {
            throw new LicenseException("文件写入出错:" + ie.getMessage() + ",证书写入异常,请联系供应商");
        } catch (Exception ex) {
            throw new LicenseException("证书异常,请联系供应商");
        } finally {
            IoUtil.close(outputStream);
            IoUtil.close(outputStreamWriter);
        }
    }

    /**
     * 存在的license.txt文件检查处理
     * @param licenseTxtPath
     * @param subject
     */
    private static void existLicenseFileCheck(String licenseTxtPath, String subject) {
        FileOutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;

        try {
            // 读取加密文件内容
            BufferedInputStream in = FileUtils.getInputStream(licenseTxtPath);
            String inContent = IoUtil.readUtf8(in);

            // 解密后的内容
            String decryptStr = licenseTxtAES.decryptStr(inContent);
            String[] split = StringUtils.split(decryptStr, ";");
            // 项目名称
            String subjectName = split[0];
            // 时间
            String dateTime = split[1];

            // 判断项目名称是否一致
            if (!subject.equals(subjectName)) {
                throw new LicenseException("证书异常,请联系供应商");
            }


            LocalDateTime fileDatedTime = LocalDateTime.parse(dateTime, LOCALDATETIME_YYYY_MM_DD_HH_MM_SS);
            // 将当前获取的时间 +5分钟
            LocalDateTime fiveLocalDateTime = fileDatedTime.plusMinutes(5);


            File licenseTxtFile = FileUtils.file(licenseTxtPath);

            // 创建流
            outputStream = new FileOutputStream(licenseTxtFile);
            // 新new OutputStreamWriter对象，记得关闭回收
            outputStreamWriter = IoUtil.getUtf8Writer(outputStream);
            String content = subjectName + ";" + fiveLocalDateTime.format(LOCALDATETIME_YYYY_MM_DD_HH_MM_SS);
            // 加密重写后的内容
            String encryptContent = licenseTxtAES.encryptHex(content);
            int c;
            for (int i = 0; i < encryptContent.length(); i++) {
                c = encryptContent.charAt(i);
                outputStreamWriter.write((char) c);
            }
            IoUtil.flush(outputStreamWriter);

        } catch (IOException ie) {
            throw new LicenseException("文件写入出错:" + ie.getMessage() + ",证书写入异常,请联系供应商");
        } catch (Exception ex) {
            throw new LicenseException("证书异常,请联系供应商:" + ex.getMessage());
        } finally {
            IoUtil.close(outputStream);
            IoUtil.close(outputStreamWriter);
        }
    }

    public static void main(String[] args) {
        String content = "BTBOX-BOOT;2023-11-11 11:55:50";
        AES aes = new AES("CBC", "PKCS7Padding",
                // 密钥，可以自定义
                "jkadsivvBqkwwpx&".getBytes(),
                // iv加盐，按照实际需求添加
                "$qcagkspqNatupyf".getBytes());

        // 加密为16进制表示
        String encryptHex = aes.encryptHex(content);

        System.out.println("encryptHex = " + encryptHex);
        // 解密
        String decryptStr = aes.decryptStr(encryptHex);

        System.out.println("decryptStr = " + decryptStr);
    }

}