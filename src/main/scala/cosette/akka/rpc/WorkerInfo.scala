package cosette.akka.rpc

/**
 * cosette.akka.rpc
 *
 * @Auther: Cosette
 * @Date: 2020/5/20 09:57
 * @Description:
 */
class WorkerInfo(val id: String, var memory: Int, var cores: Int) {

  var lastUpdateTime: Long = _

  override def toString: String = s"WorkerInfo($id, $memory, $cores)"
}
