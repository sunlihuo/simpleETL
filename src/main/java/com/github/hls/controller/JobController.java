package com.github.hls.controller;

import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobMonitorDO;
import com.github.hls.service.SimpleJobServer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class JobController {
    @Resource
    private SimpleJobServer simpleJobServer;
    @Resource
    private DefaultMQPushConsumer rocketMQConsumer;
    @Resource
    private DefaultMQProducer rocketMQProducer;


    @RequestMapping("/job")
    public List<SimpleJobDO> job(SimpleJobDO simpleJobDO){
        return simpleJobServer.queryJob(simpleJobDO);
    }

    @RequestMapping("/sendmq")
    public String sendmq(SimpleJobDO simpleJobDO) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message();
        message.setTopic("simple_job");
        message.setTags("notify");
        message.setBody("ffffffffff".getBytes());
        rocketMQProducer.send(message);
        return "ffff";
    }

}
