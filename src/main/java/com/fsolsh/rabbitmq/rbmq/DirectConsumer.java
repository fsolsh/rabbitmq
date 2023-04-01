package com.fsolsh.rabbitmq.rbmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @author andy
 * 直连交换机-消费者
 */
@Component
public class DirectConsumer {

    /**
     * 创建交换机并且绑定队列1（绑定routingKeyOne）
     *
     * @param msg
     * @param channel
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = DirectProducer.EXCHANGE_DIRECT),
            value = @Queue(value = DirectProducer.EXCHANGE_DIRECT_QUEUE_ONE, durable = "true"),
            key = DirectProducer.EXCHANGE_DIRECT_ROUTING_KEY_ONE
    ), concurrency = "2", ackMode = "MANUAL")
    @RabbitHandler
    public void exchangeDirectRoutingKeyOne(String msg, Channel channel, Message message) throws Exception {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", direct 1 receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 创建交换机并且绑定队列2（绑定routingKeyTwo）
     *
     * @param msg
     * @param channel
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = DirectProducer.EXCHANGE_DIRECT),
            value = @Queue(value = DirectProducer.EXCHANGE_DIRECT_QUEUE_TWO, durable = "true"),
            key = DirectProducer.EXCHANGE_DIRECT_ROUTING_KEY_TWO
    ), concurrency = "2", ackMode = "MANUAL")
    @RabbitHandler
    public void exchangeDirectRoutingKeyTwo(String msg, Channel channel, Message message) throws Exception {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", direct 2 receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
