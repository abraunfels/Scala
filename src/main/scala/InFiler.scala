import java.io.RandomAccessFile
import util.Properties
import scala.collection._

trait InFiler {
    val fileName: String
    val defaultBlockSize = 256 * 1024 //256Kb
    val totalChunks = getNumberOfChunks

    private def getNumberOfChunks: Int = {
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        (randomAccessFile.length / defaultBlockSize).toInt
      } finally {
        randomAccessFile.close
      }
    }
    def readLines(chunkIndex: Int, offset: Int): Array[String] = {
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        val byteBuffer = Array.ofDim[Byte](defaultBlockSize)
        var seek = (chunkIndex - 1) * defaultBlockSize - offset
        randomAccessFile.seek(seek)
        val len = randomAccessFile.read(byteBuffer)
        val rawString = new String(byteBuffer, 0, len)
        rawString.split(Properties.lineSeparator)
      } finally {
        randomAccessFile.close
      }
    }
}
