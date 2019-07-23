import java.io.RandomAccessFile
import util.Properties
import scala.collection._

trait InFiler extends Worker{
    val fileName: String
    val defaultBlockSize = 256 * 1024 //256Kb

    def Read {
      val startTime = System.currentTimeMillis
      var sum: Int = 0
      var offset: Int = 0
      for (i <- 1 to getNumberOfChunks+1) {
        var tmp = Array[String]()
        tmp = readLines(i, offset)
        offset = tmp(tmp.size - 1).length
      }
      val endTime = System.currentTimeMillis
      println("Total time in millis: " + (endTime - startTime))
    }

    private def getNumberOfChunks: Int = {
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        (randomAccessFile.length / defaultBlockSize).toInt
      } finally {
        randomAccessFile.close
      }
    }
    private def readLines(chunkIndex: Int, offset: Int): Array[String] = {
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        val byteBuffer = Array.ofDim[Byte](defaultBlockSize)
        val seek = (chunkIndex - 1) * defaultBlockSize - offset
        randomAccessFile.seek(seek)
        val len = randomAccessFile.read(byteBuffer)
        val rawString = new String(byteBuffer, 0, len)
        rawString.split(Properties.lineSeparator)
      } finally {
        randomAccessFile.close
      }
    }
}
