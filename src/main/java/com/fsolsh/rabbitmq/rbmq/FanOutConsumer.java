package com.fsolsh.rabbitmq.rbmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import static com.fsolsh.rabbitmq.rbmq.FanOutProducer.*;

/**
 * @author andy
 * 扇出交换机-消费者
 */
@Component
public class FanOutConsumer {

    /**
     * 创建交换机并且绑定队列（队列1）
     *
     * @param msg
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = EXCHANGE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(value = EXCHANGE_FANOUT_QUEUE_ONE, durable = "true")), concurrency = "2", ackMode = "MANUAL")
    @RabbitHandler
    public void exchangeFanOutQueueOne(String msg, Message message, Channel channel) throws Exception {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", fanOut 1 receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }

    /**
     * 创建交换机并且绑定队列（队列2）
     *
     * @param msg
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange(value = EXCHANGE_FANOUT, type = ExchangeTypes.FANOUT),
            value = @Queue(value = EXCHANGE_FANOUT_QUEUE_TWO, durable = "true")), concurrency = "2", ackMode = "MANUAL")
    @RabbitHandler
    public void exchangeFanOutQueueTwo(String msg, Message message, Channel channel) throws Exception {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", fanOut 2 receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }


}
