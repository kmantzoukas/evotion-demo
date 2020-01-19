import java.sql.{Connection, DriverManager}
import java.util

import com.google.gson.Gson
import org.apache.spark.{SparkConf, SparkContext}

import scala.annotation.switch

object EvotionAnalyticsService {

  val host = "10.207.1.102"
  val port = "54310"

  def main(args: Array[String]): Unit = {
	
    val conf = new SparkConf()
    val sc = new SparkContext(conf)

    val data = sc.textFile(s"hdfs://$host:$port/evotion_data/user_pta_test_result").map(line =>{
      val columns = line.split('|')
      val patientId = columns(0).toString
      val haPtaResult = columns(4).toFloat
      (patientId, haPtaResult)
    })

    var results = Array[(String, Float, Float, Float,Float, Int)]();

    args match {
      case Array(_)  => {
        results = data
          .groupByKey
          .map(mapResults).collect
      }
      case Array(_,_)  => {
        results = data.filter(tuple => args(1).split(",").toSet.contains(tuple._1))
          .groupByKey
          .map(mapResults).collect
      }
    }

    val buf  = new util.ArrayList[Patient]();
    for(result <- results){
      buf.add(new Patient(result._1, result._2, result._3, result._4, result._5, result._6))
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

    connection.close
  }

  private def mapResults(tuple: (String, Iterable[Float])) = {
    val sum = tuple._2.sum
    val count = tuple._2.size
    val min = tuple._2.min
    val max = tuple._2.max
    val diff = max - min

    (tuple._1, (sum/count), min, max, diff, count)
  }
}
