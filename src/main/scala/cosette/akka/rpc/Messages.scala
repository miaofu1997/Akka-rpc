package cosette.akka.rpc

/**
 * cosette.akka.rpc
 *
 * @Auther: Cosette
 * @Date: 2020/5/20 10:03
 * @Description: 既可以进行模式匹配 又可以封装数据
 *              case class默认实现了序列化接口
 *              要封装数据的就用case class，不需要就用case object
 */

//Worker发送给Master的注册消息，case class默认实现了序列化接口
case class RegisterWorker(id: String, memory: Int, cores: Int)

//Master发送给Worker注册成功的消息
case object RegisteredWorker

//Worker发送给Master的心跳消息
case class Heartbeat(workerId: String)

//Worker自己给自己发送的消息(内部消息)
case object SendHeartbeat

//Master发送给自己的消息，用于检查超时的Worker
case object CheckTimeOutWorker
