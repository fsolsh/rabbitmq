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
 * 扇出交换机-生产者
 */
@Component
public class FanOutProducer {

    public static final String EXCHANGE_FANOUT = "exchange.fanout";
    // 绑定扇形交换机的队列1
    public static final String EXCHANGE_FANOUT_QUEUE_ONE = "exchange.fanout.queue.one";
    // 绑定扇形交换机的队列2
    public static final String EXCHANGE_FANOUT_QUEUE_TWO = "exchange.fanout.queue.two";
    private final String BUSS_TYPE = "USER_LOGIN";

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送FanOut消息
     */
    @PostConstruct
    private void sendMessage() {
        AtomicInteger bussId = new AtomicInteger();
        Constant.SCHEDULED_POOL.scheduleAtFixedRate(() -> {
            MsgDto msg = new MsgDto();
            msg.setPlatform("FanOut");
            msg.setBussId(String.valueOf(bussId.incrementAndGet()));
            msg.setBussType(BUSS_TYPE);
            System.out.println("send fanOut msg bussId : " + bussId.get());

            try {
                //routingKey 在fanOut模式不使用，会在direct和topic模式使用
                rabbitTemplate.convertAndSend(EXCHANGE_FANOUT, "", JSON.toJSONString(msg));
            } catch (Exception e) {
                System.out.println("there is something wrong with fanOut producer:" + e.getMessage());
            }

        }, 0L, 1L, TimeUnit.SECONDS);
    }
}
