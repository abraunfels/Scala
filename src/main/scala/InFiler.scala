import java.io.RandomAccessFile
import util.Properties

trait InFiler {
    val fileName: String
    val defaultBlockSize = 256 * 1024 //256Kb

    private[this] var alreadyReadenBytes = 0

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
        val rawString = new String(byteBuffer, 0, len)
        val Lines =
          if (rawString.last != '\n')  rawString.split(Properties.lineSeparator).init
          else rawString.split(Properties.lineSeparator)
        alreadyReadenBytes += len - (if (rawString.last != '\n') rawString.split(Properties.lineSeparator).last.length else 0)//Идиотиизм, но по-другому не придумала
        Lines
      } finally {
        randomAccessFile.close
      }
    }
}
