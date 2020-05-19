package cosette.akka.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

/**
 * cosette.akka.rpc
 *
 * @Auther: Cosette
 * @Date: 2020/5/19 16:03
 * @Description: Worker Actor最好在构造方法执行之后，receive方法执行之前向Master建立连接
 */
class Worker extends Actor {

  //生命周期方法
  //在构造方法之后，receive方法之前执行一次preStart
  override def preStart(): Unit = {

    //Worker向Master建立网络连接，“小弟找老大”
    //要知道ActorSystem名字、ip、port端口号、/user、下面的Actor名字，才能找到对应Actor
    val masterRef = context.actorSelection("akka.tcp://MASTER_ACTOR_SYSTEM@localhost:8888/user/MASTER_ACTOR")

    //Worker向Master发送注册的信息
    masterRef ! "register"
  }

  override def receive: Receive = {
    case "hi" => {
      println("hiii~~~")
    }
  }
}

object Worker {

  def main(args: Array[String]): Unit = {

    val host = "localhost"
    val port = 9999
    //tcp属于长链接，连上了就一直连接着；http短链接，一下就断了
    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
        """.stripMargin

    //通过配置工厂，解析字符串
    val config: Config = ConfigFactory.parseString(configStr)
    //创建ActorSystem
    val actorSystem = ActorSystem("WORKER_ACTOR_SYSTEM", config)

    //创建Worker Actor, actorOf是个同步的方法
    val workeractor = actorSystem.actorOf(Props[Worker], "WORKER_ACTOR")

    //自己给自己发送消息
    workeractor ! "hi"


  }
}