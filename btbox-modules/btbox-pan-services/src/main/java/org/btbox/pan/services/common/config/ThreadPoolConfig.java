package org.btbox.pan.services.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description: 线程池配置类
 * @author: BT-BOX
 * @createDate: 2024/1/25 15:45
 * @version: 1.0
 */
@Configuration
public class ThreadPoolConfig {

    @Bean(name = "eventListenerTaskExecutor")
    public ThreadPoolTaskExecutor eventListenerTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(10);
        taskExecutor.setKeepAliveSeconds(200);
        taskExecutor.setQueueCapacity(2048);
        taskExecutor.setThreadNamePrefix("event-listener-thread");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        return taskExecutor;
    }

}