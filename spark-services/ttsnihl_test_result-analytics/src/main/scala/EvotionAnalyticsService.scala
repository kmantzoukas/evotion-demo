import java.sql.{Connection, DriverManager}
import java.util

import com.google.gson.Gson
import org.apache.spark.{SparkConf, SparkContext}

object EvotionAnalyticsService {

  val host = "10.207.1.102"
  val port = "54310"

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf()
    val sc = new SparkContext(conf)

    val data = sc.textFile(s"hdfs://$host:$port/evotion_data/ttsnihl_test_result").map(line => {
      val columns = line.split('|')
      val patientId = columns(0).toString
      val startDate = columns(4).toLong
      val endDate = columns(5).toLong
      (patientId, (startDate, endDate))
    })

    var results = Array[(String, Long, Long, Long, Long, Int)]()

    args match {
      case Array(_) => {
        results =
          data
            .map(tuple => {
              val patientId = tuple._1
              val timediff = tuple._2._2 - tuple._2._1

              (patientId, timediff)
            })
            .groupByKey
            .map(mapResults).collect
      }
      case Array(_, _) => {
        results =
          data
            .map(tuple => {
              val patientId = tuple._1
              val timediff = tuple._2._2 - tuple._2._1

              (patientId, timediff)
            })
            .filter(item => args(1).split(",").toSet.contains(item._1))
            .groupByKey
            .map(mapResults).collect
      }
    }

    val buf = new util.ArrayList[Patient]();
    for (result <- results) {
      buf.add(new Patient(result._1, result._2, result._3, result._4, result._5, result._6))
    }

    var connection: Connection = null
    try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      connection = DriverManager.getConnection("jdbc:mysql://10.207.1.102:3306/evotion", "root", "root")
      val statement = connection.createStatement
      val requestId = args(0)
      val json = (new Gson).toJson(buf)
      statement.executeUpdate(s"INSERT INTO evotion.results(requestId,result) VALUES('${requestId}','${json}')")
    } catch {
      case e: Exception => e.printStackTrace
    }

  }

  private def mapResults(tuple: (String, Iterable[Long])) = {
    val sum = tuple._2.sum / 1000
    val count = tuple._2.size
    val min = tuple._2.min / 1000
    val max = tuple._2.max / 1000
    val diff = (max / 1000) - (min / 1000)

    (tuple._1, (sum / count), min, max, diff, count)
  }
}