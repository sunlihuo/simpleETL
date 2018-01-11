package com.github.hls.controller;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import com.github.hls.base.enums.SimpleJobEnum;
import com.github.hls.base.task.SimpleJobTask;
import com.github.hls.domain.BaseQueryInfo;
import com.github.hls.domain.JqGridVO;
import com.github.hls.domain.SimpleJobDO;
import com.github.hls.domain.SimpleJobMonitorDO;
import com.github.hls.service.SimpleJobServer;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;

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

    @RequestMapping("/queryList")
    public JqGridVO<List<SimpleJobDO>> queryList(SimpleJobDO simpleJobDO, BaseQueryInfo queryInfo){
        PageHelper.startPage(queryInfo.getPage(), queryInfo.getRows()); // 核心分页代码
        //PageHelper.orderBy("inputDate desc");

        final Page<SimpleJobDO> simpleJobDOS = simpleJobServer.queryJob(simpleJobDO);

        JqGridVO<List<SimpleJobDO>> result = new JqGridVO<>(simpleJobDOS.getResult(), simpleJobDOS);
        return result;
    }

    @RequestMapping("/update")
    public String update(SimpleJobDO simpleJobDO, String oper, String id){
        if ("del".equalsIgnoreCase(oper)) {
            simpleJobDO.setSimpleJobId(Long.valueOf(id));
            simpleJobDO.setStatus(SimpleJobEnum.STATUS.STOP.name());
            simpleJobServer.update(simpleJobDO);
        } else if ("add".equalsIgnoreCase(oper)) {
            simpleJobServer.insert(simpleJobDO);
        } else {
            simpleJobServer.update(simpleJobDO);
        }
        return "success";
    }

    @ResponseBody
    @RequestMapping("/queryMonitorList")
    public JqGridVO<List<SimpleJobMonitorDO>> queryMonitorList(SimpleJobDO simpleJobDO, BaseQueryInfo queryInfo){
        PageHelper.startPage(queryInfo.getPage(), queryInfo.getRows()); // 核心分页代码
        //PageHelper.orderBy("inputDate desc");

        final Page<SimpleJobMonitorDO> simpleJobDOS = simpleJobServer.queryMonitorList(simpleJobDO);

        JqGridVO<List<SimpleJobMonitorDO>> result = new JqGridVO<>(simpleJobDOS.getResult(), simpleJobDOS);
        return result;
    }

    @RequestMapping("/updateMonitor")
    public String updateMonitor(SimpleJobMonitorDO simpleJobMonitorDO, String oper, String id){
        if ("del".equalsIgnoreCase(oper)) {
            simpleJobMonitorDO.setSimpleJobMonitorId(Long.valueOf(id));
            simpleJobServer.update(simpleJobMonitorDO);
        } else if ("add".equalsIgnoreCase(oper)) {
            simpleJobServer.insert(simpleJobMonitorDO);
        } else {
            simpleJobServer.update(simpleJobMonitorDO);
        }
        return "success";
    }


    @RequestMapping("/job")
    public String job(SimpleJobDO simpleJobDO){
        simpleJobTask.handleHttp(simpleJobDO);
        /*new Thread(() -> simpleJobTask.handleHttp(simpleJobDO));*/
        return "success";
    }

    @RequestMapping("/create/topic")
    public String topic(SimpleJobDO simpleJobDO) throws MQClientException {
        rocketMQProducer.createTopic(rocketMQProducer.getCreateTopicKey(), "simple_job", rocketMQProducer.getDefaultTopicQueueNums());
        return "success";
    }

    @RequestMapping("/sendmq")
    public String sendmq(SimpleJobDO simpleJobDO) throws InterruptedException, RemotingException, MQClientException, MQBrokerException, UnsupportedEncodingException {
        Message message = new Message();
        message.setTopic("simple_job");
        message.setTags("notify");
        message.setBody(simpleJobDO.getJobName().getBytes("UTF-8"));
        SendResult sendResult = rocketMQProducer.send(message);
        System.out.println(sendResult.getSendStatus()+";"+sendResult.toString());
        return "success";
    }


}
