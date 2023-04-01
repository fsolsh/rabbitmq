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
 * 主题交换机-生产者
 */
@Component
public class TopicProducer {

    /**
     * Topic交换机接收的消息RoutingKey必须是多个单词，以 . 分割
     * Topic交换机与队列绑定时的routingKey可以指定通配符，#:代表0个或多个词; *:代表1个词
     */

    // 主題交换机定义
    public static final String EXCHANGE_TOPIC = "exchange.topic";
    // 主題交换机队列定义1
    public static final String EXCHANGE_TOPIC_QUEUE_ONE = "exchange.topic.queue.one";
    // 主題交换机队列定义1
    public static final String EXCHANGE_TOPIC_QUEUE_TWO = "exchange.topic.queue.two";
    // 主題交换机队列路由Key定义1
    public static final String EXCHANGE_TOPIC_ROUTING_KEY_ONE = "#.routingKey.#";
    // 主題交换机队列路由Key定义2
    public static final String EXCHANGE_TOPIC_ROUTING_KEY_TWO = "routingKey.*";
    // 案例KEY1 可以被EXCHANGE_TOPIC_ROUTING_KEY_ONE匹配不能被EXCHANGE_TOPIC_ROUTING_KEY_TWO匹配
    public static final String EXCHANGE_TOPIC_CASE_KEY_ONE = "topic.routingKey.caseOne";
    // 案例KEY2 可以同时被EXCHANGE_TOPIC_ROUTING_KEY_ONE和EXCHANGE_TOPIC_ROUTING_KEY_TWO匹配
    public static final String EXCHANGE_TOPIC_CASE_KEY_TWO = "routingKey.caseTwo";
    private final String BUSS_TYPE = "USER_RESET";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送Topic消息
     */
    @PostConstruct
    private void sendMessage() {
        AtomicInteger bussId = new AtomicInteger();
        Random r = new Random();
        SCHEDULED_POOL.scheduleAtFixedRate(() -> {
            MsgDto msg = new MsgDto();
            msg.setPlatform("Topic");
            msg.setBussId(String.valueOf(bussId.incrementAndGet()));
            msg.setBussType(BUSS_TYPE);
            System.out.println("send topic msg bussId : " + bussId.get());
            int routingKey = r.nextInt(2) + 1;
            if (routingKey == 1) {
                rabbitTemplate.convertAndSend(EXCHANGE_TOPIC, EXCHANGE_TOPIC_CASE_KEY_ONE, JSON.toJSONString(msg));
            } else {
                rabbitTemplate.convertAndSend(EXCHANGE_TOPIC, EXCHANGE_TOPIC_CASE_KEY_TWO, JSON.toJSONString(msg));
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
