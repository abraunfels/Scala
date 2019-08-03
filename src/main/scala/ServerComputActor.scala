import java.io.File

import akka.actor.Actor
import akka.event.Logging
import org.mongodb.scala.bson.ObjectId

final case class FileID (val file: File, val id: ObjectId)

class ServerComputActor() extends Actor {
  val log = Logging(context.system, this)

  override def receive: Receive = {
    case FileID(file, objID) =>
      MongoProcessor.saverCollect(objID, new Processor(file.getAbsolutePath).getJSONResult)
  }
}

