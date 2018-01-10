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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
public class HtmlController {

    @RequestMapping("/indexHtml")
    public String indexHtml(Map<String,Object> map){

        map.put("menuHtml","!!!!!!!!!!!!!!!!!!!!from TemplateController.helloHtml");
        return "index";
    }

    @RequestMapping("/simpleJobHtml")
    public String simpleJobHtml(Map<String,Object> map){
        map.put("url", "queryList");
        return "table/jqgrid";
    }

}
