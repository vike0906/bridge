package com.vike.bridge.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;


/**
 * @Author: lsl
 * @Date: Create in 2018/11/16
 */
@Component
@Slf4j
public class ApplicationStart implements ApplicationListener<ApplicationReadyEvent> {


    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {

        log.info("应用已启动完成");

    }

}
