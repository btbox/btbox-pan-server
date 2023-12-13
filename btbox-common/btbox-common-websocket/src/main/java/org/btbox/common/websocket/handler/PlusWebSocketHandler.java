package org.btbox.common.websocket.handler;

import lombok.extern.slf4j.Slf4j;
import org.btbox.common.websocket.constant.WebSocketConstants;
import org.btbox.common.websocket.dto.WebSocketMessageDto;
import org.btbox.common.websocket.holder.WebSocketSessionHolder;
import org.btbox.common.websocket.utils.WebSocketUtils;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.List;

/**
 * WebSocketHandler 实现类
 *
 * @author zendwang
 */
@Slf4j
public class PlusWebSocketHandler extends AbstractWebSocketHandler {

    /**
     * 连接成功后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long userId = (Long) session.getAttributes().get(WebSocketConstants.LOGIN_USER_KEY);
        WebSocketSessionHolder.addSession(userId, session);
        log.info("[connect] sessionId: {},userId:{}", session.getId(), userId);
    }

    /**
     * 处理发送来的文本消息
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("PlusWebSocketHandler, 连接：" + session.getId() + "，已收到消息:" + message.getPayload());
        Long userId = (Long) session.getAttributes().get(WebSocketConstants.LOGIN_USER_KEY);
        List<Long> userIds = List.of(userId);
        WebSocketMessageDto webSocketMessageDto = new WebSocketMessageDto();
        webSocketMessageDto.setSessionKeys(userIds);
        webSocketMessageDto.setMessage(message.getPayload());
        WebSocketUtils.publishMessage(webSocketMessageDto);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
    }

    /**
     * 心跳监测的回复
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        WebSocketUtils.sendPongMessage(session);
    }

    /**
     * 连接出错时
     *
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("[transport error] sessionId: {} , exception:{}", session.getId(), exception.getMessage());
    }

    /**
     * 连接关闭后
     *
     * @param session
     * @param status
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(WebSocketConstants.LOGIN_USER_KEY);
        WebSocketSessionHolder.removeSession(userId);
        log.info("[disconnect] sessionId: {},userId:{}", session.getId(), userId);
    }

    /**
     * 是否支持分片消息
     *
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

}
