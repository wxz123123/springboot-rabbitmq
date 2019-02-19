package com.example.demo.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description 消费者类，包含多个消费者，每个消费者坚挺相关的队列
 * @Author wxz
 * @Date 2019/2/18 16:38
 */
@Component
@Slf4j
public class Cosumer {
    /**
     * 测试直连交换机获取消息
     * @param msg
     */

    @RabbitListener(queues = "hello")
    public void process0(String msg){
        log.info("消费者0  获取hello队列消息："+msg);
    }




    /**
     * 测试消费扇形交换机
     * @param msg
     */
    @RabbitListener(queues = "a")
    public void process1(String msg){
        log.info("消费者1  从a队列获取消息："+msg);
    }
    @RabbitListener(queues = "b")
    public void process2(String msg){
        log.info("消费者2  从b队列获取消息："+msg);
    }
    @RabbitListener(queues = "c")
    public void process3(String msg){
        log.info("消费者3  从c队列获取消息："+msg);
    }





    /**
     * 测试消费主题交换机
     * @param msg
     */
    @RabbitListener(queues = "one")
    public void process4(String msg){
        log.info("消费者4  从one队列获取消息："+msg);
    }
    @RabbitListener(queues = "two")
    public void process5(String msg){
        log.info("消费者5  从two队列获取消息："+msg);
    }
}
