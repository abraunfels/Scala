import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import akka.event.Logging

import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.server.directives.FileInfo

import akka.stream.ActorMaterializer
import akka.util.Timeout

import org.mongodb.scala.FindObservable
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.Future

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

final case class MsgOK(status: String, val id: String)
final case class MsgERR(status: String, val error: String)

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val msgOKFormat = jsonFormat2(MsgOK)
  implicit val msgERRFormat = jsonFormat2(MsgERR)
  implicit val dailyFormat = jsonFormat2(Daily)
  implicit val operatorItemFormat = jsonFormat5(OperatorItem)
  implicit object ObjectIdJsonFormat extends JsonFormat[ObjectId] {
    def write(obj: ObjectId): JsValue = JsString(obj.toString)
    def read(json: JsValue): ObjectId = json match {
      case JsString(str) => new ObjectId(str)
      case _ => throw new DeserializationException(" string expected")
    }
  }
  implicit val fileDataFormat = jsonFormat2(FileData)
}

object MainServer extends Directives with JsonSupport{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10, TimeUnit.SECONDS)

  val sCA = system.actorOf(Props[ServerComputActor], name = "serverComputingActor")

  def tempDestination(fileInfo: FileInfo): File =
    File.createTempFile(fileInfo.fileName, "", new File("D:/Files/SCALA/Scala-taskOne"))
  //As i think, The value of destination path need to be initialized in another place

  val route = concat(
    path("uploadStats") {
      storeUploadedFile("csv/stats", tempDestination) {
        case (metadata, file) =>
          val res = MongoProcessor.saverGridFS(file)
            .map(objId => {
              sCA ! (FileID(file, objId))
              MsgOK("ok", objId.toString)
            })
          //.recover{ //Not worked with this part
          // case except: Exception =>
          //     MsgERR("error", except.toString)
          //}
          file.deleteOnExit() //This file is not deleted, while app is working. Why?
          complete(res.toFuture())
      }
    },
    path("getStat") {
      parameters('id) { (id) => {
      val res: FindObservable[FileData] = MongoProcessor.getCollectFile(id)
      complete (res.toFuture())
      }
      }
    }
  )

  val (host, port) = ("localhost", 80)
  val bindingFuture: Future[ServerBinding] =  Http().bindAndHandle(route, host, port)

  val log =  Logging(system.eventStream, "go-ticks")

  bindingFuture.failed.foreach { ex =>
   log.error(ex, "Failed to bind to {}:{}!", host, port)
  }
}


