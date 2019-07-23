import scala.util.matching.Regex
object TaskOne extends App {
  val test = new Processor("txs2.csv")
  val res = test.processFile
  for ((k, v) <- res)
   v.display

}

