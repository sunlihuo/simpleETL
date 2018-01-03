package com.github.hls.base.rocketMQ;

import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageProcessor{

    public boolean handleMessage(Message message) {
        System.out.println("receive : " + message.toString());
        return true;
    }

}