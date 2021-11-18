package com.github.hls.simplejob.service;//package com.github.hls.simplejob.service;
//
//import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
//import com.alibaba.rocketmq.client.producer.SendResult;
//import com.alibaba.rocketmq.common.message.Message;
//import lombok.extern.log4j.Log4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//
//@Service
//@Log4j
//public class RocketMQProducerServer {
//
//    @Resource
//    private DefaultMQProducer rocketMQProducer;
//
//    @Value("${rocketmq.producer.topic}")
//    private String topic;
//    @Value("${rocketmq.producer.tag}")
//    private String tag;
//
//    public void sendJobName(String jobName) {
//        try {
//            Message message = new Message();
//            message.setTopic(topic);
//            message.setTags(tag);
//            message.setBody(jobName.getBytes("UTF-8"));
//            SendResult sendResult = rocketMQProducer.send(message);
//            log.info("发送MQ消息 sendJobName=" + sendResult);
//        } catch (Exception e) {
//           log.error("rocketMQProducer.send(message) error", e);
//        }
//    }
//}
