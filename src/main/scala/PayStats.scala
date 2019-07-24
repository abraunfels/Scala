import scala.collection._

class PayStats (val operator: String, var sum: Float, date: String){ //это главный конструктор
  var quantity : Int = 1

  var daily = new mutable.HashMap [String, Tuple2[Float, Int]]
  daily (date) = Tuple2(sum, 1)

  val quantityMaxPays = 5
  var maxPays: List[Float] = List()
  maxPays.::(sum)
  //конец главного конструктора

  def addPayt (amount: Float, date: String){
    sum += amount
    quantity += 1
    if (daily.contains(date)){
      daily (date) = Tuple2(daily(date)._1 + amount, daily(date)._2 + 1)
    }
    else {
      daily(date) = Tuple2 (amount, 1)
    }
    maxPays = addToList(amount, maxPays)
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

  def display: Unit ={
    println ("Operator " + operator)
    println ("Sum of payments: " + sum)
    println ("Quantity of paymetns: " + quantity)
    print ("Top-5 payments")
    for (el <- maxPays)
      print(" " + el)
    printf("\n")
    println("Daily average")
    for ((k, v) <- daily)
      print (k+ " " + v)
    printf("\n")
  }
}
