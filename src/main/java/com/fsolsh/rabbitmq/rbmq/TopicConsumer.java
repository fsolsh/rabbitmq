package com.fsolsh.rabbitmq.rbmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author andy
 * 主题交换机-消费者
 */
@Component
public class TopicConsumer {

    /**
     * 创建交换机并且绑定队列1（绑定routingKeyOne）
     *
     * @param msg
     * @param channel
     * @param message
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = TopicProducer.EXCHANGE_TOPIC, type = ExchangeTypes.TOPIC),
            value = @Queue(value = TopicProducer.EXCHANGE_TOPIC_QUEUE_ONE, durable = "true"),
            key = TopicProducer.EXCHANGE_TOPIC_ROUTING_KEY_ONE
    ), concurrency = "2", ackMode = "MANUAL")
    @RabbitHandler
    public void exchangeTopicRoutingKeyOne(String msg, Channel channel, Message message) throws Exception {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", topic 1 receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 创建交换机并且绑定队列1（绑定routingKeyTwo）
     *
     * @param msg
     * @param channel
     * @param message
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = TopicProducer.EXCHANGE_TOPIC, type = ExchangeTypes.TOPIC),
            value = @Queue(value = TopicProducer.EXCHANGE_TOPIC_QUEUE_TWO, durable = "true"),
            key = TopicProducer.EXCHANGE_TOPIC_ROUTING_KEY_TWO
    ), concurrency = "2", ackMode = "MANUAL")
    @RabbitHandler
    public void exchangeTopicRoutingKeyTwo(String msg, Channel channel, Message message) throws Exception {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", topic 2 receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
