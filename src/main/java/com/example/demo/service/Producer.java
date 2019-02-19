package com.example.demo.service;

import com.example.demo.config.RabbitMqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description 生产者，包含多个生产者，每个生产者给不同的队列发送消息
 * @Author wxz
 * @Date 2019/2/18 16:03
 */
@Component
@Slf4j
public class Producer {
    @Autowired
    private AmqpTemplate rabbitTemplate;
    /**
     * 测试默认交换机,默认交换机是一个名字为空""的直连交换机direct exchange
     * 发送消息时，不指定交换机时都会走默认交换机
     * 第一个参数表示routing key，第二个参数即消息
     * convertAndSend(String var1, Object var2)
     */
    public void senderDefault(String msg){
        rabbitTemplate.convertAndSend("hello",msg);
        log.info("生产者1  通过默认交换机发送消息："+msg);
    }
    /**
     * 测试直连交换机 DirectExchange
     * convertAndSend(String var1, String var2, Object var3)
     * 第一个参数表示交换机，第二个参数表示routing key，第三个参数即消息
     */
    public void senderDirect(String msg){
        rabbitTemplate.convertAndSend(RabbitMqConfig.directExchange,"hello",msg);
        log.info("生产者2  通过直连交换机发送消息："+msg);
    }
    /**
     * 测试扇形交换机 FanoutExchange
     * FanoutExchange 扇形交换机没有路由的概念，所以发送消息时，路由routting key 参数为空
     * convertAndSend(String var1, String var2, Object var3)
     * 第一个参数表示交换机，第二个参数表示routing key，第三个参数即消息
     */
    public void senderFanout(String msg){
        rabbitTemplate.convertAndSend(RabbitMqConfig.fanoutExchange,"",msg);
        log.info("生产者3  通过扇形交换机发送消息："+msg);
    }
    /**
     * 测试主题交换机 TopicExchange
     * FanoutExchange
     * convertAndSend(String var1, String var2, Object var3)
     * 第一个参数表示交换机，第二个参数表示routing key，第三个参数即消息
     */
    public void senderTopic(String msg){
        //routting key为one.#，one.*都收到消息
        //rabbitTemplate.convertAndSend(RabbitMqConfig.topicExchange,"one.two",msg);

        //routting key为one.#的队列可以收到消息，one.*收不到，因为one.*只能匹配一个单词，而这里one之后有两个单词
        rabbitTemplate.convertAndSend(RabbitMqConfig.topicExchange,"one.two.three",msg);
        log.info("生产者4  通过主题交换机发送消息："+msg);
    }
}
