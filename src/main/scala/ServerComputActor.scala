import java.io.File
import akka.actor.Actor

class ServerComputActor extends Actor {
  override def receive: Receive = {
    case proc: Processor => {
      val res = proc.getJSONResult
      println (res)
    }
  }
}

