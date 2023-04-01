package com.fsolsh.rabbitmq.rbmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author andy
 * 简单队列-消费者
 */
@Component
public class SimpleConsumer {

    @RabbitListener(queuesToDeclare = @Queue(name = SimpleProducer.AMQP_SIMPLE_QUEUE), concurrency = "2", ackMode = "MANUAL")
    public void consumerMessage(String msg, Message message, Channel channel) throws IOException {
        try {
            System.out.println("thread -> " + Thread.currentThread().getId() + ", receive msg : " + msg);
            //确认成功
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            //确认失败（重新归队）
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }
}
