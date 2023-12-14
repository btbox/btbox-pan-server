package org.btbox.common.core.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

/**
 * @description: 密码加密工具类
 * @author: BT-BOX
 * @createDate: 2023/7/23 23:27
 * @version: 1.0
 */
public class PasswordUtil {

    /**
     * 加盐加密
     * @param password
     * @return
     */
    public static String encrypt(String password) {
        // 随机盐值
        String salt = IdUtil.simpleUUID();
        // 密码（md5(随机盐值+密码)）
        String finalPassword = SecureUtil.md5(salt + password);
        return salt + "$" + finalPassword;
    }

    /**
     * 解密
     *
     * @param password       要验证的密码（未加密）
     * @param securePassword 数据库中的加了盐值的密码
     * @return
     */
    public static boolean decrypt(String password, String securePassword) {
        boolean result = false;
        if (StrUtil.isNotBlank(password) && StrUtil.isNotBlank(securePassword)) {
            if (securePassword.length() == 65 && securePassword.contains("$")) {
                String[] securePasswordArr = securePassword.split("\\$");
                // 盐值
                String slat = securePasswordArr[0];
                String finalPassword = securePasswordArr[1];
                // 使用同样的加密算法和随机盐值生成最终加密的密码
                password = SecureUtil.md5(slat + password);
                if (finalPassword.equals(password)) {
                    result = true;
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String password = "123";
        String dbPassword = PasswordUtil.encrypt(password);
        System.out.println("加密密码：" + dbPassword);
        boolean result = PasswordUtil.decrypt("123", dbPassword);
        System.out.println("对比结果1：" + result);
        boolean result2 = PasswordUtil.decrypt("123456", dbPassword);
        System.out.println("对比结果2：" + result2);
    }

}