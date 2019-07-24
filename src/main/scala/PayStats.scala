import scala.collection._

class PayStats (val operator: String, var sum: Float, date: String){ //это главный конструктор
  var quantity : Int = 1

  val daily = new mutable.HashMap [String, (Float, Int)]
  daily (date) = Tuple2(sum, 1)

  val quantityMaxPays = 5
  var maxPays: List[Float] = List()
  maxPays.::(sum)
  //конец главного конструктора

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
