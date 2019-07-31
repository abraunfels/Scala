import java.io.File

import akka.Done
import akka.actor.{Actor, ActorSystem, CoordinatedShutdown}
import akka.stream.ActorMaterializer
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.Multipart.FormData.BodyPart
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.scaladsl.{FileIO, Framing}
import akka.util.ByteString

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationDouble
import akka.http.scaladsl.server.Directives

//дерьмицо для JSON
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import akka.actor.Actor._

class Msg
// domain model
final case class MsgOK(status: String, val id: String)
final case class MsgERR(status: String, val error: String)

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val msgOKFormat = jsonFormat2(MsgOK)
  implicit val msgERRFormat = jsonFormat2(MsgERR)
}

object MainServer extends Directives with JsonSupport{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future foreach in the end
  implicit val executionContext = system.dispatcher

  val sac = new ServerComputActor

  def tempDestination(fileInfo: FileInfo): File = //либо темповый либо неть
    File.createTempFile(fileInfo.fileName, "", new File("D:/Files")) //по-хорошему это надо как-то поменять

  val route =
    path("uploadStats") {
      val mongoGridFs = new MongoGridFS("StatsDB")
      storeUploadedFile("csv/stats", tempDestination) {
        case (metadata, file) =>
          val res = mongoGridFs.saver(file)
            .map(objId =>
            {
              //sac.receive(new Processor(file.getAbsolutePath))
              MsgOK("ok", objId.toString)

            })
            //.recover{ //с этой фигней не работает
           // case except: Exception =>
           //     MsgERR("error", except.toString)
          //}
          file.delete()
          complete(res.toFuture())
      }
    }

  val (host, port) = ("localhost", 80)
  val bindingFuture: Future[ServerBinding] =  Http().bindAndHandle(route, host, port)

  val log =  Logging(system.eventStream, "go-ticks")

  bindingFuture.failed.foreach { ex =>
   log.error(ex, "Failed to bind to {}:{}!", host, port)
  }
}


