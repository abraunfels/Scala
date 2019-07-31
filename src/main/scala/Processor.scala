import scala.collection._
import scala.util.matching.Regex

class Processor (sourceFile: String) extends {val fileName = sourceFile} with InFiler{

  def getJSONResult: Array[OperatorItem] ={
    val resMap = this.processFile
    resMap.map(x => {
      val (op: String, sum: Float, q: Int, max: Array[Float], daily: Array[(String, Float)]) = x._2.getResult
      OperatorItem(op, sum, q, max, daily.map(y => Daily(y._1, y._2)))
    }).toArray
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

import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

// domain model
final case class OperatorItem(operator: String, sum: Float, quantity: Int, max: Array[Float], daily: Array[Daily])
final case class Daily(date: String, average: Float)
//final case class Order(items: List[Item])

// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val operatorItemFormat = jsonFormat2(Daily)
  implicit val dailyFormat = jsonFormat5(OperatorItem)
}
