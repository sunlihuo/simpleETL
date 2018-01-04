package com.github.hls.controller;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.github.hls.base.task.SimpleJobTask;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.service.SimpleJobServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class JobController {
    @Resource
    private SimpleJobTask simpleJobTask;
    @Resource
    private SimpleJobServer simpleJobServer;
    @Resource
    private DefaultMQPushConsumer rocketMQConsumer;
    @Resource
    private DefaultMQProducer rocketMQProducer;


    @RequestMapping("/job")
    public String job(SimpleJobDO simpleJobDO){
        simpleJobTask.handleHttp(simpleJobDO);
        /*new Thread(() -> simpleJobTask.handleHttp(simpleJobDO));*/
        return "success";
    }

    @RequestMapping("/sendmq")
    public String sendmq(SimpleJobDO simpleJobDO) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message();
        message.setTopic("simple_job");
        message.setTags("notify");
        message.setBody("ffffffffff".getBytes());
        SendResult sendResult = rocketMQProducer.send(message);
        System.out.println(sendResult.getSendStatus()+";"+sendResult.toString());
        return "ffff";
    }

}
