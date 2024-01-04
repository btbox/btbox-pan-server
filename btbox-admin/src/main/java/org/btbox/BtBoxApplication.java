package org.btbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动程序
 *
 * @author Lion Li
 */

@SpringBootApplication
@EnableScheduling
public class BtBoxApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(BtBoxApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  BtBox-boot启动成功   ლ(´ڡ`ლ)ﾞ");
    }

}
