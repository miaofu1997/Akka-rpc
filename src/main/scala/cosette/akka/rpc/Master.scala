package cosette.akka.rpc

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import scala.collection.mutable

/**
 * cosette.akka.rpc
 *
 * @Auther: Cosette
 * @Date: 2020/5/19 16:03
 * @Description: 以后要使用Actor编程模型进行通信，要和Akka发生点关系
 */
class Master extends Actor {


  val id2Worker = new mutable.HashMap[String, WorkerInfo]()


  //在preStart中启动定时器，定期检查超时的Worker，然后剔除
  override def preStart(): Unit = {

    import context.dispatcher

    context.system.scheduler.schedule(0 millisecond, 10000 millisecond, self, CheckTimeOutWorker)

  }

  //Actor用来接收消息的方法
  override def receive: Receive = {

    //Worker发送给Master的消息
    case RegisterWorker(id, memory, cores) => {

      //建数据封装起来，保存到内存中
      val workerInfo = new WorkerInfo(id, memory, cores)

      println(workerInfo)

      //保存到内存
      id2Worker(id) = workerInfo

      //向Worker反馈一个注册成功的消息
      //Master 向Worker发送 消息
      sender() ! RegisteredWorker

    }

    //Worker发送给Master的心跳消息【周期性的】
    case Heartbeat(workerId) => {

      //根据WorkerID到id2Worker中取查找对应的WorkerInfo
      if(id2Worker.contains(workerId)) {
        //根据ID取出WorkerInfo
        val workerInfo = id2Worker(workerId)
        //获取当前时间
        val currentTime = System.currentTimeMillis()
        //更新最近一次心跳时间
        workerInfo.lastUpdateTime = currentTime
      }

    }

    case CheckTimeOutWorker => {

      //取出超时的Worker
      val currentTime = System.currentTimeMillis()

      //遍历超时的Worker
      val workers = id2Worker.values
      val deadWorkers = workers.filter(w => currentTime - w.lastUpdateTime > 10000)

      deadWorkers.foreach(w => {
        //移除超时的Worker
        id2Worker -= w.id
      })

      println("current alive worker is : " + id2Worker.size)
    }

  }
}

object Master {
  //程序执行的入口
  def main(args: Array[String]): Unit = {

    val host = "localhost"
    val port = 8888

    val configStr =
      s"""
         |akka.actor.provider = "akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname = "$host"
         |akka.remote.netty.tcp.port = "$port"
      """.stripMargin

    //首先创建一个ActorSystem【单例的，在scala中就是一个object】
    val conf = ConfigFactory.parseString(configStr)
    val actorSystem = ActorSystem("MASTER_ACTOR_SYSTEM", conf)

    //通过ActorSystem创建Actor
    //通过反射创建指定类型的Actor的实例
    val masteractor = actorSystem.actorOf(Props[Master], "MASTER_ACTOR")

    //向Actor发生消息
    //! 发生异步消息
    //masteractor.!("hello")
    //masteractor ! "hello2"

  }


}

