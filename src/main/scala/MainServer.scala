import java.io.File
import java.lang.ModuleLayer.Controller
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.directives.FileInfo
import akka.parboiled2.RuleTrace.Action
import akka.stream.ActorMaterializer
import akka.util.Timeout
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.Future
import scala.concurrent.duration.Duration

//дерьмицо для JSON
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

final case class MsgOK(status: String, val id: String)
final case class MsgERR(status: String, val error: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val msgOKFormat = jsonFormat2(MsgOK)
  implicit val msgERRFormat = jsonFormat2(MsgERR)
  implicit val operatorItemFormat = jsonFormat2(Daily)
  implicit val dailyFormat = jsonFormat5(OperatorItem)
  //implicit val fileDataFormat = jsonFormat2(FileData)
}

object MainServer extends Directives with JsonSupport{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future foreach in the end
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10, TimeUnit.SECONDS)

  val sCA = system.actorOf(Props[ServerComputActor], name = "serverComputingActor")

  def tempDestination(fileInfo: FileInfo): File = //либо темповый либо неть
    File.createTempFile(fileInfo.fileName, "", new File("D:/Files/SCALA/Scala-taskOne")) //по-хорошему это надо как-то поменять

  val route = {
    path("uploadStats") {
      storeUploadedFile("csv/stats", tempDestination) {
        case (metadata, file) =>
          val res = MongoProcessor.saverGridFS(file)
            .map(objId => {
              sCA ! (FileID(file, objId))
              MsgOK("ok", objId.toString)
            })
          //.recover{ //с этой фигней не работает
          // case except: Exception =>
          //     MsgERR("error", except.toString)
          //}
          file.deleteOnExit() //он почему то не удаляется
          complete(res.toFuture())
      }
    }
    path("getStat") {
      parameters('id) { (id) => {
      val res = MongoProcessor.getCollectFile(id)
        complete (res.toFuture())
      }
      }
    }
  }

  val (host, port) = ("localhost", 80)
  val bindingFuture: Future[ServerBinding] =  Http().bindAndHandle(route, host, port)

  val log =  Logging(system.eventStream, "go-ticks")

  bindingFuture.failed.foreach { ex =>
   log.error(ex, "Failed to bind to {}:{}!", host, port)
  }
}

final case class FileID (val file: File, val id: ObjectId)


