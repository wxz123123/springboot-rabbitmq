package com.example.demo.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description rabbitmq配置类
 * @Author wxz
 * @Date 2019/2/15 17:25
 */
@Configuration
public class RabbitMqConfig {
    public static final String directExchange="directExchange";
    public static final String fanoutExchange="fanoutExchange";
    public static final String topicExchange="topicExchange";



    /**
     * Direct Exchange 直连交换机
     * 根据消息携带的路由key值（routting key）将消息传递给对应的队列；
     * @return Exchange
     */
    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(directExchange,true,false,null);
    }
    //hello交换机用于测试直连交换机
    @Bean
    public Queue helloQueue(){
        return new Queue("hello",true,true,false,null);
    }
    //将hello队列绑定到直连交换机
    @Bean
    public Binding bindingDirectExchang(Queue helloQueue, DirectExchange directExchange){
        /**
         * 将队列helloQueue绑定到交换机directExchange，设置路由键值routting key为hello
         */
        return BindingBuilder.bind(helloQueue).to(directExchange).with("hello");
    }







    /**
     * Fanout Exchange 扇形交换机
     * 将消息路由到绑定它的所有队列，没有路由key概念，不需考虑队列的路由key（routting key）；
     * @return Exchange
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(fanoutExchange,true,false,null);
    }
    //下面a,b,c三个队列用于测试扇形交换机
    @Bean
    public Queue aQueue(){
        return new Queue("a",true,true,false,null);
    }
    @Bean
    public Queue bQueue(){
        return new Queue("b",true,true,false,null);
    }
    @Bean
    public Queue cQueue(){
        return new Queue("c",true,true,false,null);
    }
    //将队列aQueue绑定到交换机fanoutExchange，扇形交换机和路由key没关系，所以不需要设置routting key
    @Bean
    public Binding bindingAtoFanoutExchange(Queue aQueue, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(aQueue).to(fanoutExchange);
    }
    //将队列bQueue绑定到交换机fanoutExchange，扇形交换机和路由key没关系，所以不需要设置routting key
    @Bean
    public Binding bindingBtoFanoutExchange(Queue bQueue, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(bQueue).to(fanoutExchange);
    }
    //将队列cQueue绑定到交换机fanoutExchange，扇形交换机和路由key没关系，所以不需要设置routting key
    @Bean
    public Binding bindingCtoFanoutExchange(Queue cQueue, FanoutExchange fanoutExchange){
        return BindingBuilder.bind(cQueue).to(fanoutExchange);
    }










    /**
     * Topic Exchange 主题交换机
     * 队列通过路由key绑定到交换机上，交换机根据消息携带的路由key，把消息路由到一个或多个绑定的队列上；
     * 主题交换机和直连交换机有点像，但是支持路由key的模糊匹配，符号“#”匹配一个或多个词，符号“*”只匹配一个词；
     * @return Exchange
     */
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(topicExchange,true,false,null);
    }
    //下面one.onetwo,newone三个队列用于测试主题交换机
    @Bean
    public Queue oneQueue(){
        return new Queue("one",true,true,false,null);
    }
    @Bean
    public Queue twoQueue(){
        return new Queue("two",true,true,false,null);
    }
    //将one队列绑定到交换机
    @Bean
    public Binding bindingOneToTopicExchange(Queue oneQueue, TopicExchange topicExchange){
        //配置#时表示可以匹配零个或多个单词
        return BindingBuilder.bind(oneQueue).to(topicExchange).with("one.#");
    }
    @Bean
    //将onetwo队列绑定到交换机
    public Binding bindingTwoToTopicExchange(Queue twoQueue, TopicExchange topicExchange){
        //配置*时表示可以匹配一个单词
        return BindingBuilder.bind(twoQueue).to(topicExchange).with("*.two");
    }
}
