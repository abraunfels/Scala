import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{Path, Paths, StandardOpenOption}

import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.gridfs.helpers.AsynchronousChannelHelper
import org.mongodb.scala.gridfs.{AsyncInputStream, GridFSBucket, GridFSFile, GridFSUploadOptions}

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

object MongoGridFS{
  def saver(bucketName : String): ObjectId ={
    val mongoClient:MongoClient = MongoClient("mongodb://127.0.0.1:27017") //подключение к серверу
    val db: MongoDatabase = mongoClient.getDatabase("StatsDB") //подключение к БД

    val customFSBucket: GridFSBucket = GridFSBucket(db, bucketName) //создание новой корзинки c именем корзинки

    val inputPath: Path = Paths.get("D:/Files/image.png") //с какого файла читать
    val fileToRead: AsynchronousFileChannel = AsynchronousFileChannel.open(inputPath, StandardOpenOption.READ) //асинхронное чтение файла в канал
    val streamToUploadFrom: AsyncInputStream = AsynchronousChannelHelper.channelToInputStream(fileToRead)

    val trackMe = customFSBucket.uploadFromStream("FILENAME В БАЗЕ EПT", streamToUploadFrom)
    trackMe.subscribe(
      (x: ObjectId) => println("Done with: " + x), //onNext
      (t: Throwable) => println("Failed: " + t.toString) //onThrow
    )

    Await.result(trackMe.toFuture(), 2.seconds)
    streamToUploadFrom.close()
  }
}

