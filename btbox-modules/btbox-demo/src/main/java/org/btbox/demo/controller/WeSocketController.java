package org.btbox.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.btbox.common.core.domain.R;
import org.btbox.common.websocket.dto.WebSocketMessageDto;
import org.btbox.common.websocket.utils.WebSocketUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Consumer;

/**
 * WebSocket 演示案例
 *
 * @author zendwang
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/demo/websocket")
@Slf4j
public class WeSocketController {

    /**
     * 发布消息
     *
     * @param dto 发送内容
     */
    @GetMapping("/send")
    public R<Void> send(WebSocketMessageDto dto) throws InterruptedException {
        WebSocketUtils.publishMessage(dto);
        return R.ok("操作成功");
    }

    @GetMapping("/sendAll")
    public R<Void> sendAll(String message) throws InterruptedException {
        WebSocketUtils.publishAll(message);
        return R.ok("操作成功");
    }
}
