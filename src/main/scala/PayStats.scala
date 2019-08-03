import org.bson.types.ObjectId

import scala.collection._

class PayStats (val operator: String, var sum: Float, date: String){ //это главный конструктор
  var quantity : Int = 1

  val daily = new mutable.HashMap [String, (Float, Int)]
  daily (date) = Tuple2(sum, 1)

  val quantityMaxPays = 5
  var maxPays: List[Float] = List()
  maxPays.::(sum)

  def addPayt (amount: Float, date: String): PayStats = {
    sum += amount
    quantity += 1
    daily(date) = daily.get(date).map(x => (x._1 + amount, x._2 + 1)).getOrElse((amount, 1))
    maxPays = addToList(amount, maxPays)
    this
  }

  private def addToList(elem : Float, list: List[Float]): List[Float] ={
    var resList : List [Float] = List() //вот как обойтись без такой штуки??
    if (list.isEmpty){
      resList = List(elem)
    }
    if ((list.size <= quantityMaxPays) && (!list.isEmpty)){
      if (elem < list.head){
        resList = elem :: list
      }
      if (elem > list.head){
        resList = list.head :: addToList(elem, list.tail)
      }
      if (elem == list.head)
        resList = list
    }
    if (resList.size > quantityMaxPays){
      resList = resList.tail
    }
    resList
  }

  def getResult: (String, Float, Int, List[Float], List[(String, Float)]) =
    (operator, sum, quantity, maxPays, daily.map(x => (x._1, x._2._1/x._2._1)).toList)
}

//Classes for serialization
final case class FileData(val _id : ObjectId,val data: List[OperatorItem])
final case class OperatorItem(val operator: String,val sum: Float, val quantity: Int, val max: List[Float],val daily: List[Daily])
final case class Daily(val date: String, val average: Float)


