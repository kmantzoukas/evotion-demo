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

    val data = sc.textFile(s"hdfs://$host:$port/evotion_data/speech_in_babble_test").map(line =>{
      val columns = line.split('|')
      val patientId = columns(0).toString
      val taskCompletionRate = columns(4).toFloat
      val language = columns(8).toString
      val startDate = columns(9).toLong
      val endDate = columns(10).toLong
      (patientId, (language, taskCompletionRate,  startDate, endDate))
    })

    var results = Array[(String, Float, Long, Int)]()

    args match {
      case Array(_)  => {
        results =
          data
           .map(tuple => {
             val patientId = tuple._1
             val rate = tuple._2._2
             val timediff = tuple._2._4 - tuple._2._3

              (patientId, (rate, timediff))
           })
          .groupByKey
          .map(mapResults).collect
      }
      case Array(_, "en") | Array(_, "el")  => {
        results =
          data
            .map(tuple => {
              val patientId = tuple._1
              val language = tuple._2._1
              val rate = tuple._2._2
              val timeDiff = tuple._2._4 - tuple._2._3

              (patientId, (language, rate, timeDiff))
            })
            .filter(item => item._2._1 == args(1))
            .map(tuple => {
              val patientId = tuple._1
              val rate = tuple._2._2
              val timeDiff = tuple._2._3

              (patientId, (rate, timeDiff))
            })
            .groupByKey
            .map(mapResults).collect
      }
    case Array(_,_) | Array(_,_)  => {
      results =
        data
          .map(tuple => {
            val patientId = tuple._1
            val language = tuple._2._1
            val rate = tuple._2._2
            val timeDiff = tuple._2._4 - tuple._2._3

            (patientId, (language, rate, timeDiff))
          })
          .filter(item => args(1).split(",").toSet.contains(item._1))
          .map(tuple => {
            val patientId = tuple._1
            val rate = tuple._2._2
            val timeDiff = tuple._2._3

            (patientId, (rate, timeDiff))
          })
          .groupByKey
          .map(mapResults).collect
    }
    }

    val buf  = new util.ArrayList[Patient]();
    for(result <- results){
      buf.add(new Patient(result._1, result._2, result._3, result._4))
    }

    var connection:Connection = null
    try {
      Class.forName("com.mysql.cj.jdbc.Driver")
      connection = DriverManager.getConnection("jdbc:mysql://10.207.1.102:3306/evotion", "root", "root")
      val statement = connection.createStatement
      statement.executeUpdate("INSERT INTO evotion.results(requestId,result) VALUES('" + args(0) + "','" + (new Gson).toJson(buf) + "')")
    } catch {
      case e: Exception => e.printStackTrace
    }
  }

  private def mapResults(tuple: (String, Iterable[(Float, Long)])) = {
    val (rate: Float, timeSum: Long, count: Int) = {
      def foo(a:(Float, Long), b: (Float, Long, Int)) = {
        (a._1 + b._1, a._2 + b._2, b._3 + 1)
      }
      tuple._2.foldRight((0f,0l,0))(foo)
    }

    (tuple._1, rate / count, (timeSum/1000) / count, count)
  }
}