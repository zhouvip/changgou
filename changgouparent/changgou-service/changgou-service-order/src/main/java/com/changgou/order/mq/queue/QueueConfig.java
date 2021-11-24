package com.changgou.order.mq.queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 延时队列
 */
@Configuration
public class QueueConfig {

    /**
     * 创建Queue1，延时队列，会过期，过期后将数据发送给Queue2
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {
        return QueueBuilder.durable("orderDelayQueue")
                .withArgument("x-dead-letter-exchange", "orderListenerExchange")        // orderDelayQueue队列信息会过期，过期后进入到死信队列
                .withArgument("x-dead-letter-routing-key", "orderListenerQueue")
                .build();
    }

    /**
     * 创建Queue2
     */
    @Bean
    public Queue orderListenerQueue() {
        return new Queue("orderListenerQueue", true);
    }

    /***
     * 创建交换机
     */
    @Bean
    public DirectExchange orderListenerExchange(){
        return new DirectExchange("orderListenerExchange");
    }


    /***
     * 对列Queue2绑定Exchange
     */
    @Bean
    public Binding orderListenerBinding(Queue orderListenerQueue, Exchange orderListenerExchange) {
        return BindingBuilder.bind(orderListenerQueue)
                .to(orderListenerExchange)
                .with("orderListenerQueue").noargs();
    }
}