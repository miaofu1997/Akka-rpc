# Akka-rpc


This project documents my first Akka hands-on experience, including Master and Woker, how they connect and send/receive messages from each other and how they react to it.

Akka is the implementation of the **Actor** Model on the JVM, a toolkit for building highly concurrent, distributed, and resilient message-driven applications for Java and Scala. 

- Akka：基于Actor编程模型用Scala实现的通信框架。

- Actor通信模型：通过发送消息实现并发。

项目实现了一个RPC通信案例，即不同进程之间的方法调用：创建ActorSystem（单例），可以创建Actor，并且监控管理Actor。





