package org.btbox.common.websocket.interceptor;

import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.websocket.constant.WebSocketConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * WebSocket握手请求的拦截器
 *
 * @author zendwang
 */
@Slf4j
public class PlusWebSocketInterceptor implements HandshakeInterceptor {

    /**
     * 握手前
     *
     * @param request    request
     * @param response   response
     * @param wsHandler  wsHandler
     * @param attributes attributes
     * @return 是否握手成功
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpHeaders headers = request.getHeaders();
        List<String> authorizationHeaders = headers.get(WebSocketConstants.AUTHORIZATION);
        if (CollUtil.isEmpty(authorizationHeaders)) {
            log.error("websocket连接头没有请求头:" + WebSocketConstants.AUTHORIZATION);
            return false;
        }
        attributes.put(WebSocketConstants.LOGIN_USER_KEY, Long.valueOf(authorizationHeaders.get(0)));
        return true;
    }

    /**
     * 握手后
     *
     * @param request   request
     * @param response  response
     * @param wsHandler wsHandler
     * @param exception 异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
