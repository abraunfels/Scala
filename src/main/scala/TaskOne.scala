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
  MainServer
}
