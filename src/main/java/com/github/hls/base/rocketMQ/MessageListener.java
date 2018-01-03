package com.github.hls.base.rocketMQ;

import lombok.extern.log4j.Log4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Log4j
public class MessageListener implements MessageListenerConcurrently {

    @Resource
    private MessageProcessor messageProcessor;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        Message msg = msgs.get(0);

        try {
            boolean result = messageProcessor.handleMessage(msg);
            if (!result){
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        } catch (Exception e) {
            log.error("mq handle error", e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
