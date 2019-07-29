import java.io.File
import java.nio.channels.AsynchronousFileChannel
import java.nio.file.{Path, Paths, StandardOpenOption}

import org.mongodb.scala.MongoClient
import com.mongodb.async.client
import org.mongodb.scala.MongoDatabase
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.gridfs
import org.mongodb.scala.gridfs.helpers.AsynchronousChannelHelper
import org.mongodb.scala.gridfs.{AsyncInputStream, GridFSBucket, GridFSFile, GridFSUploadOptions}

import scala.concurrent.Await

import scala.concurrent.duration.DurationDouble

object TaskOne extends App {
  //val test = new Processor("txs.csv")
  //val res = test.processFile
  //res.foreach(p => p._2.display)
  //MainServer
    val mongoClient:MongoClient = MongoClient("mongodb://127.0.0.1:27017")
    val db: MongoDatabase = mongoClient.getDatabase("StatsDB")

    val customFSBucket: GridFSBucket = GridFSBucket(db, "TestImage")
    val inputPath: Path = Paths.get("D:/Files/image.png")
    val fileToRead: AsynchronousFileChannel = AsynchronousFileChannel.open(inputPath, StandardOpenOption.READ)
    val streamToUploadFrom: AsyncInputStream = AsynchronousChannelHelper.channelToInputStream(fileToRead) // Using the AsynchronousChannelHelper
  // Metadata
    val options: GridFSUploadOptions = new GridFSUploadOptions().metadata(Document("type" -> "Passport", "contactID" -> "1-22345"))
    val trackMe = customFSBucket.uploadFromStream("Passport-FirstLastName", streamToUploadFrom, options)
    trackMe.subscribe(
    (x: ObjectId) => println("Done with: " + x),
    (t: Throwable) => println("Failed: " + t.toString)
    )
    Await.result(trackMe.toFuture(), 2.seconds)
    streamToUploadFrom.close()
}

    //Location of file to be saved
    //val imageLocation = "D:\\Files\\image.png"

    //Create instance of GridFS implementation
    //val gridFs = new GridFS(db)

    //Create a file entry for the image file
    //val gridFsInputFile: GridFSFile = gridFs(new File(imageLocation))

    //Set a name on GridFS entry
    //gridFsInputFile.setFileName("image1")

    //Save the file to MongoDB
    //gridFsInputFile.save()
    //mongoClient.close()
//}

//val customFSBucket: GridFSBucket = GridFSBucket(database, "DocStore")
// Get the input stream
//val inputPath: Path = Paths.get("/home/dev/temp/test.pdf")
//val fileToRead: AsynchronousFileChannel = AsynchronousFileChannel.open(inputPath, StandardOpenOption.READ)
//val streamToUploadFrom: AsyncInputStream = AsynchronousChannelHelper.channelToInputStream(fileToRead) // Using the AsynchronousChannelHelper
// Metadata
//val options: GridFSUploadOptions = new GridFSUploadOptions().metadata(Document("type" -> "Passport", "contactID" -> "1-22345"))
//val trackMe = customFSBucket.uploadFromStream("Passport-FirstLastName", streamToUploadFrom, options)
//trackMe.subscribe(
  //(x: ObjectId) => println("Done with: " + x),
  //(t: Throwable) => println("Failed: " + t.toString)
//)
//Await.result(trackMe.toFuture(), 2.seconds)
//streamToUploadFrom.close()
//}

