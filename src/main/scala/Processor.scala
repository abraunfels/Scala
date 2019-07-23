import scala.collection._
import scala.util.matching.Regex

class Processor (sourceFile: String) extends {val fileName = sourceFile} with InFiler{
  var counter: Int = 0
  def processFile: mutable.HashMap [String, PayStats] = {
    var resStat = new mutable.HashMap [String, PayStats]
    val startTime = System.currentTimeMillis
    var offset: Int = 0
    var j: Int = 0
    for (i <- 1 to totalChunks+1) {
      var tmp = Array[String]()
      println (offset)
      tmp = readLines(i, offset)
      offset = tmp(tmp.size - 1).size
      println(tmp(0)+" "+ tmp(tmp.size - 1))
      processLines (tmp, resStat)
    }
    val endTime = System.currentTimeMillis
    println("Total time in millis: " + (endTime - startTime))
    println (counter)
    resStat
  }

  def processLines (sourceArray : Array[String], statMap : mutable.HashMap [String, PayStats]){
    for (elSrcArr <- sourceArray){
      val resStrArr = elSrcArr.split(";")
      val pattern = new Regex("\\.[0-9][0-9]")
      if ((resStrArr.size == 4) && (pattern.findFirstIn(resStrArr(3)).nonEmpty)) {
        counter += 1
        if (statMap.contains(resStrArr(2))) {
          statMap(resStrArr(2)).addPayt(resStrArr(3).toFloat, resStrArr(1))
        }
        else {
          statMap(resStrArr(2)) = new PayStats(resStrArr(2), resStrArr(3).toFloat, resStrArr(1))
        }
      }
    }
  }
}
