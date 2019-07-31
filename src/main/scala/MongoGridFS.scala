import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{Path, Paths, StandardOpenOption}

import org.mongodb.scala.{MongoClient, MongoDatabase, Observable}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.gridfs.helpers.AsynchronousChannelHelper
import org.mongodb.scala.gridfs.{AsyncInputStream, GridFSBucket, GridFSFile, GridFSUploadOptions}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationDouble
import scala.util.matching.Regex

class MongoGridFS(private val baseName: String){
  def saver(file: File): Observable[ObjectId] ={
    val mongoClient:MongoClient = MongoClient("mongodb://127.0.0.1:27017")
    val db: MongoDatabase = mongoClient.getDatabase(baseName)

    val customFSBucket: GridFSBucket = GridFSBucket(db, file.getName)
    val inputPath: Path = Paths.get(file.getAbsolutePath)
    val fileToRead: AsynchronousFileChannel = AsynchronousFileChannel.open(inputPath, StandardOpenOption.READ)
    val streamToUploadFrom: AsyncInputStream = AsynchronousChannelHelper.channelToInputStream(fileToRead)

    val fileName:String = (".*.csv".r findFirstIn file.getName).get
    val trackMe = customFSBucket.uploadFromStream(fileName, streamToUploadFrom)

    //streamToUploadFrom.close() этот поток надо где-то закрывать!?
    trackMe
  }
}
