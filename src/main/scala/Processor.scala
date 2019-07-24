import scala.collection._
import scala.util.matching.Regex

class Processor (sourceFile: String) extends {val fileName = sourceFile} with InFiler{
  def processFile: mutable.HashMap [String, PayStats] = {
    val resStat = new mutable.HashMap [String, PayStats]
    val startTime = System.currentTimeMillis
    while (!checkFinish){
      val tmp =
        if (resStat.isEmpty) readLines().tail
        else readLines()
      processLines (tmp, resStat)
    }
    val endTime = System.currentTimeMillis
    println("Total time in millis: " + (endTime - startTime))
    resStat
  }

  def processLines (sourceArray : Array[String], statMap : mutable.HashMap [String, PayStats]){
    sourceArray.foreach(x =>
        { val Array(id, date, operator, amount)= x.split(";");
          statMap(operator) = statMap.get(operator).map( x => statMap(operator).addPayt(amount.toFloat, date)).getOrElse(new PayStats(operator, amount.toFloat, date))
        })
  }
}
