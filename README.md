# 工作原理
![image](https://user-images.githubusercontent.com/33198812/229307215-5566d374-5516-4a69-b487-716025a2e93b.png)


# 名词介绍
    Broker:接收和分发消息的应用，RabbitMQ Server就是Message Broker
    Connection: publisher / consumer和 broker之间的TCP连接
    Channel:如果每一次访问RabbitMQ都建立一个Connection，在消息量大的时候建立TCP Connection的开销将是巨大的，效率也较低。Channel是在connection 内部建立的逻辑连接，如果应用程序支持多线程，通常每个thread创建单独的channel进行通讯，AMQP method包含了channel id 帮助客户端和message broker识别 channel，所以channel 之间是完全隔离的。Channel作为轻量级的Connection极大减少了操作系统建TCP connection的开销
    Exchange: message 到达 broker 的第一站，根据分发规则，匹配查询表中的 routing key，分发消息到queue 中去。常用的类型有: direct (point-to-point), topic(publish-subscribe) and fanout(multicast)
    Routing Key:生产者将消息发送到交换机时会携带一个key,来指定路由规则
    binding Key:在绑定Exchange和Queue时，会指定一个BindingKey,生产者发送消息携带的RoutingKey会和bindingKey对比，若一致就将消息分发至这个队列
    vHost 虚拟主机:每一个RabbitMQ服务器可以开设多个虚拟主机每一个vhost本质上是一个mini版的RabbitMQ服务器，拥有自己的 "交换机exchange、绑定Binding、队列Queue"，更重要的是每一个vhost拥有独立的权限机制，这样就能安全地使用一个RabbitMQ服务器来服务多个应用程序，其中每个vhost服务一个应用程序。

# 交换机类型

![image](https://user-images.githubusercontent.com/33198812/229307232-5004a3fe-15e5-47c7-8834-2a8570ccb48f.png)

    1.direct Exchange(直接交换机)
    匹配路由键，只有完全匹配消息才会被转发
    
    2.Fanout Excange（扇出交换机）
    将消息发送至所有的队列
    
    3.Topic Exchange(主题交换机)
    将路由按模式匹配，此时队列需要绑定要一个模式上。符号“#”匹配0个或多个词，“*”匹配1个词
    
    4.Header Exchange(比较少用)
    在绑定Exchange和Queue的时候指定一组键值对，header为键，根据请求消息中携带的header进行路由

# 工作模式
    1.简单模式（直接使用队列，不使用交换机）
    2.direct Exchange(直接交换机)
    3.Fanout Excange（扇出交换机）
    4.Topic Exchange(主题交换机)

    以上每种模式消费者都可以多线程消费、人工ACK（详见demo）

# 保证消息的稳定性
    1.消息持久化
    RabbitMQ的消息默认存在内存中的，一旦服务器意外挂掉，消息就会丢失    
    消息持久化需做到三点：
    Exchange设置持久化
    Queue设置持久化
    Message持久化发送：发送消息设置发送模式deliveryMode=2，代表持久化消息

    2.ACK确认机制
    多个消费者同时收取消息，收取消息到一半，突然某个消费者挂掉，要保证此条消息不丢失，就需要acknowledgement机制，就是消费者消费完要通知服务端，服务端才将数据删除
    
    以上两点就解决了，即使一个消费者出了问题，没有同步消息给服务端，还有其他的消费端去消费，保证了消息不丢的case。

# 设置集群镜像模式
    RabbitMQ三种部署模式：
    1）单节点模式：最简单的情况，非集群模式，节点挂了，消息就不能用了。业务可能瘫痪，只能等待。    
    2）普通模式：默认的集群模式，某个节点挂了，该节点上的消息不能用，有影响的业务瘫痪，只能等待节点恢复重启可用（必须持久化消息情况下）。    
    3）镜像模式：把需要的队列做成镜像队列，存在于多个节点，属于RabbitMQ的HA方案    
    为什么设置镜像模式集群，因为队列的内容仅仅存在某一个节点上面，不会存在所有节点上面，所有节点仅仅存放消息结构和元数据。

# 消息补偿机制
    持久化的消息，保存到硬盘过程中，当前队列节点挂了，存储节点硬盘又坏了，消息丢了，怎么办？    
    产线网络环境太复杂，所以不知数太多，消息补偿机制需要建立在消息要写入DB日志，发送日志，接受日志，两者的状态必须记录。    
    然后根据DB日志记录check 消息发送消费是否成功，不成功，进行消息补偿措施，重新发送消息处理。

# 如何实现延迟队列
    RabbitMQ本身没有延迟队列，需要靠TTL和DLX模拟出延迟的效果

    TTL（Time To Live）
    RabbitMQ可以针对Queue和Message设置 x-message-tt，来控制消息的生存时间，如果超时，则消息变为dead letter（死信）
    RabbitMQ针对队列中的消息过期时间有两种方法可以设置。    
    A: 通过队列属性设置，队列中所有消息都有相同的过期时间。    
    B: 对消息进行单独设置，每条消息TTL可以不同。    
    如果同时使用，则消息的过期时间以两者之间TTL较小的那个数值为准。消息在队列的生存时间一旦超过设置的TTL值，就成为dead letter
    
    DLX (Dead-Letter-Exchange)
    RabbitMQ的Queue可以配置x-dead-letter-exchange 和x-dead-letter-routing-key（可选）两个参数，如果队列内出现了dead letter，则按照这两个参数重新路由。    
    x-dead-letter-exchange：出现dead letter之后将dead letter重新发送到指定exchange    
    x-dead-letter-routing-key：指定routing-key发送    
    队列出现dead letter的情况有：    
    消息或者队列的TTL过期    
    队列达到最大长度    
    消息被消费端拒绝（basic.reject or basic.nack）并且requeue=false
    
    利用DLX，当消息在一个队列中变成死信后，它能被重新publish到另一个Exchange。这时候消息就可以重新被消费，以此来实现延迟效果。
