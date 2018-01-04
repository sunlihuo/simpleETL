package com.github.hls.service;

import lombok.extern.log4j.Log4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service@Log4j
public class RocketMQProducerServer {

    @Resource
    private DefaultMQProducer rocketMQProducer;

    public void sendJobName(String jobName) {
        try {
            Message message = new Message();
            message.setTopic("simple_job");
            message.setTags("notify");
            message.setBody(jobName.getBytes("UTF-8"));
            rocketMQProducer.send(message);
        } catch (Exception e) {
           log.error("rocketMQProducer.send(message) error", e);
        }
    }
}
