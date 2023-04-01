package com.fsolsh.rabbitmq.rbmq;

import com.alibaba.fastjson.JSON;
import com.fsolsh.rabbitmq.base.MsgDto;
import com.fsolsh.rabbitmq.base.Constant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author andy
 * 简单队列-生产者
 */
@Component
public class SimpleProducer {

    public static final String AMQP_SIMPLE_QUEUE = "amqp.simple.queue";
    private final String BUSS_TYPE = "USER_REGISTER";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送Simple消息
     */
    @PostConstruct
    private void sendMessage() {
        AtomicInteger bussId = new AtomicInteger();
        Constant.SCHEDULED_POOL.scheduleAtFixedRate(() -> {
            MsgDto msg = new MsgDto();
            msg.setPlatform("Simple");
            msg.setBussId(String.valueOf(bussId.incrementAndGet()));
            msg.setBussType(BUSS_TYPE);
            System.out.println("send simple msg bussId : " + bussId.get());
            rabbitTemplate.convertAndSend(AMQP_SIMPLE_QUEUE, JSON.toJSONString(msg));
        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
