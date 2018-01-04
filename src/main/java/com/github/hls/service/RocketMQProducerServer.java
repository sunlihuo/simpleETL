package com.github.hls.service;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import lombok.extern.log4j.Log4j;
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
            SendResult sendResult = rocketMQProducer.send(message);
            log.info("发送MQ消息 sendJobName=" + sendResult);
        } catch (Exception e) {
           log.error("rocketMQProducer.send(message) error", e);
        }
    }
}
