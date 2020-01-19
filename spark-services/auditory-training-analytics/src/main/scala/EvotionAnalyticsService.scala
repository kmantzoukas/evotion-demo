import java.sql.{Connection, DriverManager}
import java.util

import com.google.gson.Gson
import org.apache.spark.{SparkConf, SparkContext}

object EvotionAnalyticsService {

  val host = "10.207.1.102"
  val port = "54310"

  case class AuditoryTraining(patientId:String,language: String, startTime: Long, endTime: Long)

  def main(args: Array[String]): Unit = {
	
    val conf = new SparkConf()
    val sc = new SparkContext(conf)

    val data = sc.textFile(s"hdfs://$host:$port/evotion_data/auditory_training").map(line =>{
      val columns = line.split('|')
      val patientId = columns(0).toString
      val language = columns(1).toString
      val startDate = columns(2).toLong
      val endDate = columns(3).toLong
      (patientId, (language, startDate, endDate))
    }).map(record => {
      val patientId = record._1
      val language = record._2._1
      val startDate = record._2._2
      val endDate = record._2._3

      AuditoryTraining(patientId , language, startDate, endDate)
    })

    var results = Array[(String, String, Long, Long, Long, Long, Int)]();

    args match {
      case Array(_)  => {
        results = data.map(tuple => (tuple.patientId, tuple.endTime - tuple.startTime))
          .groupByKey
          .map(mapResults).collect
      }
      case Array(_, "en") | Array(_, "el") => {
        results = data.filter(tuple => args(1).equals(tuple.language))
          .map(tuple => (tuple.patientId, tuple.endTime - tuple.startTime))
          .groupByKey
          .map(mapResults).collect
      }
      case Array(_,_)  => {
        results = data.filter(tuple => args(1).split(",").toSet.contains(tuple.patientId))
          .map(tuple => (tuple.patientId, tuple.endTime - tuple.startTime))
          .groupByKey
          .map(mapResults).collect
      }
    }

    val buf = new util.ArrayList[Patient]();
    for(result <- results){
      buf.add(new Patient(result._1, result._2, result._3.toLong, result._4.toLong, result._5.toLong, result._6.toLong, result._7))
    }

    var connection:Connection = null
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
    connection.close

    sc.stop
  }

  private def mapResults(tuple: (String, Iterable[Long])) = {
    val sum = tuple._2.sum
    val count = tuple._2.size
    val avg = (sum/count) / 1000
    val s = (avg % 60)
    val m = (avg/60) % 60
    val h = (avg/60/60) % 24
    val min = tuple._2.min / 1000
    val max = tuple._2.max / 1000
    val diff = max - min

    (tuple._1, "%02d:%02d:%02d".format(h, m, s), avg, min, max, diff, count)
  }
}
