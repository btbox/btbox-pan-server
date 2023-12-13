package org.btbox.common.license.handler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.constant.HttpStatus;
import org.btbox.common.core.domain.R;
import org.btbox.common.license.exception.LicenseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

/**
 * @description: 证书异常处理器
 * @author: BT-BOX
 * @createDate: 2023/11/17 17:38
 * @version: 1.0
 */
@Slf4j
@RestControllerAdvice
public class LicenseExceptionHandler {

    @ExceptionHandler(LicenseException.class)
    public R<Void> handlerLicenseException(LicenseException e) {
        log.error("证书管理出现异常: " + e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN, "证书管理出现异常: " + e.getMessage());
    }

}