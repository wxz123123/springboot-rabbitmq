# springboot-rabbitmq
springboot集成rabbitmq demo

### 1 RabbitMQ 消息队列简介
* rabbitmq对外提供给的api基本对象： ConnectionFaction（连接工厂），Connection（socket 连接），Channel(通道)；
    ConnectionFaction用于创建Connection,Connneciton提供socket连接，业务操作在Channel接口上完成，包括声明交换机、队列、队列绑定交换机、发送消息
    都是在Channel上完成。
* 核心组成部分： 
<pre><code>
    生产者：发送消息到交换机
    交换机（Exchange）：将收到的消息，根据路由规则路由到特定的队列中
    队列（queue）：存储消息，等待消费者来获取消息
    消费者：接收消息并执行相关业务操作；
</code></pre>
* 工作过程：生产者----》交换机----》队列----》消费者
* 工作原理：
rabbitmq通过ConnectionFaction连接工厂创建connection连接，通过connection连接创建通道channel,
在通道中创建交换机和队列，并将队列绑定到交换机当中；生产者向交换机发送消息，并带上队列的路由key值，交换机将
消息传送到对应的队列当中（前提是这个队列必须绑定在该交换机当中），队列接收到消息之后并将消息发送给消费者，
消费者获取到消息，并执行相应业务操作，执行完成后，消费者返回一个通知给队列确认已收到消息
（消费者确认消息环节很重要，如果忘记确认，消息会一直存在队列当中，再下次消费者重启后，会将消息再次发送给消费者）
，队列收到确认后删除消息，过程完毕。
* 作用于场景：常用于处理请求中不需要即时返回且耗时的业务操作，如用户付款之后需要发送购买成功的短信到用户手机场景
这个场景中，支付是核心业务，需要即时处理，而发送短信是非即时的，我们可以把发送短信这部分业务先放到消息中间件中，
后面再执行，而支付操作马上执行就返回给用户，相比于执行支付和发短信操作再响应，这样就大大提升了响应效率。

### 2 Queue（队列） 介绍
 队列用于存储交换机路由过来的消息，并等待消费者来消费
 ##### 2.1 队列的重要属性参数
 Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
<pre><code>
     name:队列名字
     参数列表：
     * durable：是否持久化
     
     * exclusive：是否排外的；有两个作用，
       一：当连接关闭时connection.close()该队列是否会自动删除；
       二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，没有任何问题，
       如果是排外的，会对当前队列加锁，其他通道channel是不能访问的；
       一般等于true的话用于一个队列只能有一个消费者来消费的场景
     
     * autoDelete：不在使用时（没有任何绑定时），是否由rabbit自动删除
     
     * arguments：扩展参数，可以设置消息过期时间等
</code></pre>

### 3 Exchange（交换机） 介绍
 交换机负责接收生产者发送的消息，并根据指定的路由规则，将消息路由到指定的队列。
 ##### 3.1 交换机的重要属性参数
 DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
 <pre><code>
 type：交换机类型
 name:交换机名称
 Durability:是否持久化，如果持久化，rabbitmq重启后交换机还存在
 Auto-delete：当所有与该交换机绑定的队列都完成对交换机的使用之后，是否删除该交换机
 Arguments：扩展参数
 </code></pre>
##### 3.2 交换机的几种主要类型
<pre><code>
（1）默认交换机：默认交换机是rabbitmq预先声明好的名字为空的direct Exchange直连交换机；
     我们每声明一个队列，都会被自动绑定到默认交换机上面，绑定的路由键routting key与队列名称一样；
     当我们发送一条消息，没有写明交换机的时候，会走默认交换机
     适用场景：普通一对一发短信消息  
     
（2）direct exchange(直连交换机)：根据消息携带的路由key值（routting key）将消息传递给对应的队列；
     

（3）fanout exchange(扇形交换机)：将消息路由到绑定它的所有队列，没有路由key概念，不需考虑队列的路由key（routting key）；
     适用场景：群发短信，邮件等

（4）topic exchange(主题交换机)：队列通过路由key绑定到交换机上，交换机根据消息携带的路由key，把消息路由到一个或多个绑定的队列上；
     这个类型和直连交换机有点像，但是支持路由binding key（队列绑定到交换机的路由键）的模糊匹配，符号“#”匹配一个或多个单词，符号“*”只能匹配一个单词；
     比如：我们有两个队列one,two,
           one绑定到交换机的routting key为one.#
           two绑定到交换机的routting key为one.*
           如果生产者发送消息时routting key 为one.two,那么one,和two队列都可以收到消息
           如果生产者发送消息时routting key 为one.two.three,那么one队列收到消息,two队列没有收到消息，因为two队列绑定的routting key 是*号，只能匹配一个单词
（5）headers exchange（头交换机）：  

   
</code></pre>
### 4 rabiitmq 六种工作模式 介绍
* 简单模式：一个生产者，一个消费者（发送消息使用direct exchange直连交换机或者默认交换机）

* work模式：一个生产者，多个消费者，每个消费者获取到的消息唯一（发送消息使用direct exchange直连交换机或者默认交换机）。
  
* 订阅模式：一个生产者发送的消息会被多个消费者获取（发送消息使用fanout exchange扇形交换机）。
  
* 路由模式：发送消息到交换机并且要指定路由key ，消费者将队列绑定到交换机时需要指定路由key（发送消息使用direct exchange直连交换机）。
  
* topic模式：和路由模式相似，但是支持路由binding key（队列绑定到交换机的路由键）的模糊匹配，“#”匹配一个词或多个词，“*”只匹配一个词（发送消息使用topic exchange主题交换机）。

本例是个人学习笔记，欢迎大家一起交流学习，扣扣：568962516