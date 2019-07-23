import java.io.RandomAccessFile
import util.Properties
import scala.collection._

trait InFiler {
    val fileName: String
    val defaultBlockSize = 256 * 1024 //256Kb

    private[this] var alreadyReadenBytes = 0

    private def getNumberOfChunks: Int = {
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        (randomAccessFile.length / defaultBlockSize).toInt
      } finally {
        randomAccessFile.close
      }
    }

    def checkFinish: Boolean = {
      var flagFinish : Boolean = false
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        val temp = randomAccessFile.length
        if (alreadyReadenBytes == randomAccessFile.length) {
          flagFinish = true
        }
        flagFinish
      } finally {
        randomAccessFile.close
      }
    }
    def readLines(): Array[String] = {
      val randomAccessFile = new RandomAccessFile(fileName, "r")
      try {
        val byteBuffer = Array.ofDim[Byte](defaultBlockSize)
        randomAccessFile.seek(alreadyReadenBytes)
        val len = randomAccessFile.read(byteBuffer)
        alreadyReadenBytes += len
        val rawString = new String(byteBuffer, 0, len)
        val Lines = rawString.split(Properties.lineSeparator)
        if (rawString.last != '\n') alreadyReadenBytes -= Lines.last.length
        Lines
      } finally {
        randomAccessFile.close
      }
    }
}
