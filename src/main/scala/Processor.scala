import scala.collection._

class Processor (sourceFile: String) extends {val fileName = sourceFile} with InFiler{

  def getJSONResult: List[OperatorItem] ={
    val resMap = this.processFile
    resMap.map(x => {
      val (op: String, sum: Float, q: Int, max: List[Float], daily: List[(String, Float)]) = x._2.getResult
      OperatorItem(op, sum, q, max, daily.map(y => Daily(y._1, y._2)))
    }).toList
  }

  private def processFile: mutable.HashMap [String, PayStats] = {
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

  private def processLines (sourceArray : Array[String], statMap : mutable.HashMap [String, PayStats]){
    sourceArray.foreach(x =>
        { val Array(id, date, operator, amount)= x.split(";");
          statMap(operator) = statMap.get(operator).map( x => statMap(operator).addPayt(amount.toFloat, date)).getOrElse(new PayStats(operator, amount.toFloat, date))
        })
  }
}
