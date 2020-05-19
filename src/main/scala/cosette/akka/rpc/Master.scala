package cosette.akka.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * cosette.akka.rpc
 *
 * @Auther: Cosette
 * @Date: 2020/5/19 16:03
 * @Description: 以后要使用Actor编程模型进行通信，要和Akka发生点关系
 */
class Master extends Actor {

  //Actor用来接收消息的方法
  override def receive: Receive = {

    case "hello" => {
      println("received a hello msg")
    }
    case "hello2" => {
      println("received a hello2 msg")
    }
      //Worker向Master发送的注册消息
    case "register" => {
      println("received a register msg from worker")
    }

  }

}

object Master {

  //程序执行的入口
  def main(args: Array[String]): Unit = {

    val host = "localhost"
    val port = 8888
    //tcp属于长链接，连上了就一直连接着；http短链接，一下就断了
    val configStr =
      s"""
        |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname = "$host"
        |akka.remote.netty.tcp.port = "$port"
        """.stripMargin
    //首先创建一个ActorSystem【单例的，就是object】
    val conf = ConfigFactory.parseString(configStr)
    val actorSystem = ActorSystem("MASTER_ACTOR_SYSTEM", conf)

    //通过ActorSystem创建Actor
    //通过反射创建指定类型Actor的实例
    val masteractor = actorSystem.actorOf(Props[Master], "MASTER_ACTOR")

    //向Actor发送消息 可以自己给自己发消息 !发送异步消息
    masteractor.!("hello")
    masteractor ! "hello2"

  }
}