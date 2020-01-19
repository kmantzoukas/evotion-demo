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

    val data = sc.textFile(s"hdfs://$host:$port/evotion_data/digit_recall_test_result").map(line =>{
      val columns = line.split('|')
      val patientId = columns(0).toString
      val haDigitRecallScore = columns(4).toInt
      val language = columns(6).toString
      val startDate = columns(7).toLong
      val endDate = columns(8).toLong
      (patientId, (language, haDigitRecallScore,  startDate, endDate))
    })

    var results = Array[(String, Float, Long, Int)]()

    args match {
      case Array(_)  => {
        results =
          data
           .map(tuple => {
             val patientId = tuple._1
             val score = tuple._2._2
             val timediff = tuple._2._4 - tuple._2._3

              (patientId, (score, timediff))
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
              val score = tuple._2._2
              val timeDiff = tuple._2._4 - tuple._2._3

              (patientId, (language, score, timeDiff))
            })
            .filter(item => item._2._1 == args(1))
            .map(tuple => {
              val patientId = tuple._1
              val score = tuple._2._2
              val timeDiff = tuple._2._3

              (patientId, (score, timeDiff))
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
            val score = tuple._2._2
            val timeDiff = tuple._2._4 - tuple._2._3

            (patientId, (language, score, timeDiff))
          })
          .filter(item => args(1).split(",").toSet.contains(item._1))
          .map(tuple => {
            val patientId = tuple._1
            val score = tuple._2._2
            val timeDiff = tuple._2._3

            (patientId, (score, timeDiff))
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

  private def mapResults(tuple: (String, Iterable[(Int, Long)])) = {
    val (haDigitRecallScoreSum: Int, timeSum: Long, count: Int) = {
      def foo(a:(Int, Long), b: (Int, Long, Int)) = {
        (a._1 + b._1, a._2 + b._2, b._3 + 1)
      }
      tuple._2.foldRight((0,0l,0))(foo)
    }

    (tuple._1, haDigitRecallScoreSum.toFloat / count, (timeSum/1000) / count, count)
  }
}