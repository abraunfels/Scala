import scala.collection._

class PayStats (val operator: String, var sum: Float, date: String){ //это главный конструктор
  var quantity : Int = 1

  var daily = new mutable.HashMap [String, Tuple2[Float, Int]]
  daily (date) = Tuple2(sum, 1)

  //var maxPays  = new mutable.MutableList[Float]
  //maxPays += sum
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
    /*if (maxPays.size <= 5){

    }*/
  }

  def display: Unit ={
    println ("Operator " + operator)
    println ("Sum of payments: " + sum)
    println ("Quantity of paymetns: " + quantity)
    print ("Top-5 payments")
    //for (el <- maxPays)
    //  print(" " + el)
    printf("\n")
    println("Daily average")
    for ((k, v) <- daily)
      print (k+ " " + v)
    printf("\n")
  }
}
