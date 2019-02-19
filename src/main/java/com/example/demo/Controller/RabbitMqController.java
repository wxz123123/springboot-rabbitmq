package com.example.demo.Controller;
import com.example.demo.service.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Author wxz
 * @Date 2019/2/18 16:44
 */
@RestController
@Slf4j
public class RabbitMqController {
    @Autowired
    private Producer producer;
    @GetMapping("/senderDefault")
    public void senderDefault(@RequestParam("msg")String msg){
        log.info("走默认交换机的消息开始发送");
        producer.senderDefault(msg);

    }
    @GetMapping("/senderDirect")
    public void senderDirect(@RequestParam("msg")String msg){
        log.info("走直连交换机的消息开始发送");
        producer.senderDirect(msg);
    }
    @GetMapping("/senderFanout")
    public void senderFanout(@RequestParam("msg")String msg){
        log.info("走扇形交换机的消息开始发送");
        producer.senderFanout(msg);
    }
    @GetMapping("/senderTopic")
    public void senderTopic(@RequestParam("msg")String msg){
        log.info("走主题交换机的消息开始发送");
        producer.senderTopic(msg);
    }
}
