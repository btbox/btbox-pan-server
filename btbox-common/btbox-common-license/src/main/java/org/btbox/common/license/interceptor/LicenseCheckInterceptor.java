package org.btbox.common.license.interceptor;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.constant.HttpStatus;
import org.btbox.common.core.domain.R;
import org.btbox.common.core.utils.MessageUtils;
import org.btbox.common.json.utils.JsonUtils;
import org.btbox.common.license.utils.LicenseVerify;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.servlet.HandlerInterceptor;


@Slf4j
public class LicenseCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        LicenseVerify licenseVerify = new LicenseVerify();

        //校验证书是否有效
        boolean verifyResult = licenseVerify.verify();

        if(verifyResult){
            return true;
        }else{
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.FORBIDDEN);
            response.getWriter().write(JsonUtils.toJsonString(R.fail(HttpStatus.FORBIDDEN, MessageUtils.message("license.expired"))));
            return false;
        }
    }

}
