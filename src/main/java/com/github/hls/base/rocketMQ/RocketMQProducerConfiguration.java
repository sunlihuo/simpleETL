package com.github.hls.base.rocketMQ;

import com.github.hls.base.exception.RocketMQException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j
public class RocketMQProducerConfiguration {

    @Value("${rocketmq.producer.groupName}")
    private String groupName;
    @Value("${rocketmq.producer.namesrvAddr}")
    private String namesrvAddr;
    @Value("${rocketmq.producer.instanceName}")
    private String instanceName;
    @Value("${rocketmq.producer.maxMessageSize}")
    private int maxMessageSize; //4M
    @Value("${rocketmq.producer.sendMsgTimeout}")
    private int sendMsgTimeout;

    @Bean
    public DefaultMQProducer getRocketMQProducer() throws RocketMQException {
        if (StringUtils.isBlank(this.groupName)) {
            throw new RocketMQException("groupName is blank");
        }
        if (StringUtils.isBlank(this.namesrvAddr)) {
            throw new RocketMQException("nameServerAddr is blank");
        }
        if (StringUtils.isBlank(this.instanceName)) {
            throw new RocketMQException("instanceName is blank");
        }
        DefaultMQProducer producer;
        producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);
        producer.setInstanceName(instanceName);
        producer.setMaxMessageSize(this.maxMessageSize);
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        producer.setVipChannelEnabled(false);
        try {
            producer.start();
            // shutdown时关闭producer
            RocketMQProducerConfiguration.ShutdownHook shutdownHook = new RocketMQProducerConfiguration.ShutdownHook();
            shutdownHook.setProducer(producer);
            Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
            log.info(String.format("producer is start ! groupName:[%s],namesrvAddr:[%s]"
                    , this.groupName, this.namesrvAddr));
        } catch (MQClientException e) {
            log.error(String.format("producer is error %s", e.getMessage(), e));
            throw new RocketMQException(e);
        }
        return producer;
    }

    private class ShutdownHook implements Runnable {
        private DefaultMQProducer producer;

        public void setProducer(DefaultMQProducer producer) {
            this.producer = producer;
        }

        @Override
        public void run() {
			/*
			 * 应用退出时，要调用shutdown来清理资源，关闭网络连接，从MetaQ服务器上注销自己
			 * 注意：我们建议应用在JBOSS、Tomcat等容器的退出钩子里调用shutdown方法
			 */
            if (null != producer) {
                log.info("shuttingdown producer..");
                producer.shutdown();
            }
        }
    }
}
