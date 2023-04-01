package com.fsolsh.rabbitmq.rbmq;

import com.alibaba.fastjson.JSON;
import com.fsolsh.rabbitmq.base.MsgDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.fsolsh.rabbitmq.base.Constant.SCHEDULED_POOL;

/**
 * @author andy
 * 直连交换机-生产者
 */
@Component
public class DirectProducer {

    // 直连交换机定义
    public static final String EXCHANGE_DIRECT = "exchange.direct";
    // 直连交换机队列定义1
    public static final String EXCHANGE_DIRECT_QUEUE_ONE = "exchange.direct.queue.one";
    // 直连交换机队列定义2
    public static final String EXCHANGE_DIRECT_QUEUE_TWO = "exchange.direct.queue.two";
    // 直连交换机路由KEY定义1
    public static final String EXCHANGE_DIRECT_ROUTING_KEY_ONE = "exchange.direct.routing.key.one";
    // 直连交换机路由KEY定义2
    public static final String EXCHANGE_DIRECT_ROUTING_KEY_TWO = "exchange.direct.routing.key.two";
    private final String BUSS_TYPE = "USER_LOGOUT";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送Direct消息
     */
    @PostConstruct
    private void sendMessage() {
        AtomicInteger bussId = new AtomicInteger();
        Random r = new Random();
        SCHEDULED_POOL.scheduleAtFixedRate(() -> {
            MsgDto msg = new MsgDto();
            msg.setPlatform("Direct");
            msg.setBussId(String.valueOf(bussId.incrementAndGet()));
            msg.setBussType(BUSS_TYPE);
            System.out.println("send direct msg bussId : " + bussId.get());
            int routingKey = r.nextInt(2) + 1;
            if (routingKey == 1) {
                rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, EXCHANGE_DIRECT_ROUTING_KEY_ONE, JSON.toJSONString(msg));
            } else {
                rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, EXCHANGE_DIRECT_ROUTING_KEY_TWO, JSON.toJSONString(msg));
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
