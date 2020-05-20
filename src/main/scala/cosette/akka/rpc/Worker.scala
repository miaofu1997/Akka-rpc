package cosette.akka.rpc

import java.util.UUID

import scala.concurrent.duration._
import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.{Config, ConfigFactory}

/**
 * cosette.akka.rpc
 *
 * @Auther: Cosette
 * @Date: 2020/5/19 16:03
 * @Description: Worker Actor最好在构造方法执行之后，receive方法执行之前向Master建立连接
 */
class Worker extends Actor {


  var masterRef: ActorSelection = _

  val WORKER_ID = UUID.randomUUID().toString

  //生命周期方法
  //在构造方法之后，receive方法之前，执行一次preStart
  override def preStart(): Unit = {

    //Worker向Master建立网络连接
    masterRef = context.actorSelection("akka.tcp://MASTER_ACTOR_SYSTEM@localhost:8888/user/MASTER_ACTOR")

    //Worker向Master发送注册的信息
    masterRef ! RegisterWorker(WORKER_ID, 4096, 8)
  }

  override def receive: Receive = {

    //Master反馈给Worker的消息
    case RegisteredWorker => {

      //导入隐式转换
      import context.dispatcher
      //启动一个定时器，定期向Master发送心跳，使用Akka框架封装的定时器
      //定期给自己发送消息，然后再给Master发送心跳
      context.system.scheduler.schedule(0 millisecond, 5000 millisecond, self, SendHeartbeat)

    }

    //自己给自己发送的消息
    case SendHeartbeat => {

      //可以进行一些逻辑判断
      //向Master发送心跳消息
      masterRef ! Heartbeat(WORKER_ID)

    }

  }
}

object Worker {

  def main(args: Array[String]): Unit = {

    val host = "localhost"
    val port = 9999

    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
      """.stripMargin

    //通过一个配置工厂，解析字符串
    val config: Config = ConfigFactory.parseString(configStr)
    //创建ActorSystem
    val actorSystem = ActorSystem("WORKER_ACTOR_SYSTEM", config)

    //创建Worker Actor
    val workeractor = actorSystem.actorOf(Props[Worker], "WORKER_ACTOR")

    //自己给自己发送消息
    //workeractor ! "hi"


  }


}