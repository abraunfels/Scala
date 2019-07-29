import java.io.File

import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
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

object MainServer {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future foreach in the end
  implicit val executionContext = system.dispatcher

  def tempDestination(fileInfo: FileInfo): File =
    File.createTempFile(fileInfo.fileName, ".tmp", new File("D:/Files"))
  //{
  //  val f = new File(fileInfo.fileName)
  //  f.createNewFile()
  //  f
  //}

  val route =
    path("uploadStats") {
      storeUploadedFiles("csv/stats", tempDestination) { files =>
        val finalStatus = files.foldLeft(StatusCodes.OK) {
          case (status, (metadata, file)) =>

            //тут обработка файла для монго
            //file.delete()
            status
        }
        complete(finalStatus)

        //val done: Future[Done] = CoordinatedShutdown(system).run(CoordinatedShutdown.UnknownReason)
      }
    }

  val (host, port) = ("localhost", 80)
  val bindingFuture: Future[ServerBinding] =  Http().bindAndHandle(route, host, port)

  val log =  Logging(system.eventStream, "go-ticks")

  bindingFuture.failed.foreach { ex =>
   log.error(ex, "Failed to bind to {}:{}!", host, port)
  }
}
