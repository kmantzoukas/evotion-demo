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

    val table = args(1) match {
      case "PER_HOUR" => "table_ds11_1_output"
      case "PER_6HOURS" => "table_ds11_2_output"
      case "PER_DAY" => "table_ds11_3_output"
      case "PER_WEEK" => "table_ds11_4_output"
      case "PER_MONTH" => "table_ds11_5_output"
    }

    var results = Array[(String, Iterable[(String,String,String,String)])]();

    val data = sc.textFile(s"hdfs://$host:$port/evotion_data/$table").map(line =>{
      val columns = line.split('|')
      val patientId = columns(0)
      val from = columns(1)
      val to = columns(2)
      val avg = columns(3)
      val total = columns(4)
      (patientId, (from, to, avg, total))
    })

    args match {
      case Array(_, "PER_HOUR") | Array(_, "PER_6HOURS") | Array(_, "PER_DAY") | Array(_, "PER_WEEK") | Array(_, "PER_MONTH")  => {
        results = data
          .groupByKey().collect
      }
      case Array(_, "PER_HOUR",_) | Array(_, "PER_6HOURS",_) | Array(_, "PER_DAY",_) | Array(_, "PER_WEEK",_) | Array(_, "PER_MONTH",_)  => {
        results = data.filter(tuple => args(2).split(',').toSet.contains(tuple._1))
          .groupByKey.collect
      }
    }

    val buf  = new util.ArrayList[Patient]();

    for(result <- results){
      val analytics = result._2

      val buffer = new util.ArrayList[PatientAnalytic]();
      for(analytic <- analytics){
        buffer.add(new PatientAnalytic(analytic._1,analytic._2,analytic._3,analytic._4))
      }
      val p = new Patient(result._1, buffer)
      buf.add(p)
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

  }
}