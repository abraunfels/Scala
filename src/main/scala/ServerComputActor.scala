import java.io.File

import akka.actor.Actor
import akka.event.Logging
import scala.concurrent.ExecutionContext.Implicits.global

class ServerComputActor() extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case FileID(file, objID) => {
      val sndr = sender()
      val res = new Processor(file.getAbsolutePath).getJSONResult
      MongoProcessor.saverCollect(objID, res)
      //тут обработка в монго
    }
  }
}

