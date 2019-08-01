import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{Path, Paths, StandardOpenOption}

import ai.snips.bsonmacros.{CodecGen, DatabaseContext}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.gridfs.helpers.AsynchronousChannelHelper
import org.mongodb.scala.gridfs.{AsyncInputStream, GridFSBucket}
import org.mongodb.scala.{Document, FindObservable, MongoClient, MongoCollection, MongoDatabase, Observable}
import tour.Helpers._
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.types.ObjectId

import scala.concurrent.ExecutionContext
import org.mongodb.scala.model.Filters._

object MongoProcessor{
  private val _host:String = "mongodb://127.0.0.1:27017"
  private val _baseName = "StatsDB"
  private val _collectionName = "StatisticsFiles"
  val mongoClient: MongoClient = MongoClient(_host)
  val database: MongoDatabase = mongoClient.getDatabase(_baseName).withCodecRegistry(
    fromRegistries(fromProviders(classOf[FileData], classOf[OperatorItem], classOf[Daily]), DEFAULT_CODEC_REGISTRY )
  )
  val collection: MongoCollection[FileData] = database.getCollection(_collectionName)

  def saverGridFS(file: File): Observable[ObjectId] ={

    val customFSBucket: GridFSBucket = GridFSBucket(database, file.getName)
    val inputPath: Path = Paths.get(file.getAbsolutePath)
    val fileToRead: AsynchronousFileChannel = AsynchronousFileChannel.open(inputPath, StandardOpenOption.READ)
    val streamToUploadFrom: AsyncInputStream = AsynchronousChannelHelper.channelToInputStream(fileToRead)

    val fileName:String = (".*.csv".r findFirstIn file.getName).get
    val trackMe = customFSBucket.uploadFromStream(fileName, streamToUploadFrom)

    //mongoClient.close() закрывается ли он сам??
    //streamToUploadFrom.close() этот поток надо где-то закрывать!?
    trackMe
  }

  def saverCollect(objId: ObjectId, lstOp : List[OperatorItem]) = { //это выполняется синхронно
    //если сделать класс как актор то что делать с предыдущей функцией
    //или можно оставить синхронно
    collection.insertOne(FileData(objId, lstOp)).results()
  }

  def getCollectFile(id: String): FindObservable[FileData] = {
    collection.find(equal("_id", new  ObjectId(id)))
  }

}

