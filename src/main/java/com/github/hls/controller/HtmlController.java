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
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafTemplateAvailabilityProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.spring4.view.ThymeleafView;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
        map.put("jqgridJs", "var name = '孙力火';");
        return "table/simpleJob";
    }

    @RequestMapping("/simpleJobMonitorHtml")
    public String simpleJobMonitorHtml(Map<String,Object> map){
        map.put("breadcrumb1", "Home");
        map.put("breadcrumb2", "表数据管理");
        map.put("breadcrumb3", "simpleJobMonitor");

        return "table/simpleJobMonitor";
    }

}
