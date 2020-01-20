import org.apache.spark.{SparkConf, SparkContext}
import org.apache.phoenix.spark._
import org.apache.spark.rdd.RDD
import java.sql.Timestamp

object ExportHBaseTableService {
  def main(args: Array[String]): Unit = {
    
    val conf = new SparkConf().setAppName("evotion-export-data").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val tables = args

    tables.foreach( table =>{

      table match {
        case "HA_ENVRIRONMENT_DATA" => {

          val ha_environment_data: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "HA_ENVRIRONMENT_DATA",
            Seq("PATIENT_ID",
              "RECORD_DATE",
              "TYPE",
              "ENVIRONMENT_CLASSIFICATION_UNIT",
              "SOUND_PARAMETERS",
              "LOCATION",
              "HA_PROG",
              "HA_VOL",
              "PTS_TTS_RES",
              "HEARING_AID_ID",
              "SYSTEM_TIME"),
            zkUrl = Some("localhost:2181"))

          case class HaEnvironmentData(
                                        patientId:String,
                                        recordDate:Long,
                                        typ3:Short,
                                        environmentClassificationUnit:Short,
                                        soundParameters:String,
                                        location:String,
                                        haProg:Short,
                                        haVol:Short,
                                        ptsTtsRes:Short,
                                        hearingAidId:String,
                                        systemTime:Long){
            override def toString =
              s"$patientId|$recordDate|$typ3|$environmentClassificationUnit|$soundParameters|$location|$haProg|$haVol|$ptsTtsRes|$hearingAidId|$systemTime"
          }

          ha_environment_data.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val recordDate = item("RECORD_DATE").asInstanceOf[Timestamp].getTime
            val typ3 = item("TYPE").asInstanceOf[Short]
            val environmentClassificationUnit =  item("ENVIRONMENT_CLASSIFICATION_UNIT").asInstanceOf[Short]
            val soundParameters = item("SOUND_PARAMETERS").asInstanceOf[Array[Float]]
            val location = item("LOCATION").asInstanceOf[Array[String]]
            val haProg = item("HA_PROG").asInstanceOf[Short]
            val haVol = item("HA_VOL").asInstanceOf[Short]
            val ptsTtsRes = item("PTS_TTS_RES").asInstanceOf[Short]
            val hearingAidId = item("HEARING_AID_ID").asInstanceOf[String]
            val systemTime = item("SYSTEM_TIME").asInstanceOf[Timestamp].getTime

            HaEnvironmentData(patientId,
              recordDate,
              typ3,
              environmentClassificationUnit,
              soundParameters.mkString(","),
              location.mkString(","),
              haProg,
              haVol,
              ptsTtsRes,
              hearingAidId,
              systemTime).toString
          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/ha_environment_data")

        }
        case "USER_PTA_TEST_RESULT" => {

          val user_pta_test_result: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "USER_PTA_TEST_RESULT",
            Seq("PATIENT_ID",
              "RECORD_DATE",
              "LOCATION",
              "HA_PTA_DATA",
              "HA_PTA_RESULT",
              "SYSTEM_TIME"),
            zkUrl = Some("localhost:2181"))

          case class UserPtaTestResult(
                                        patientId:String,
                                        recordDate:Long,
                                        location:String,
                                        haPtaData:String,
                                        haPtaResult:Float,
                                        systemTime:Long){
            override def toString = s"$patientId|$recordDate|$location|$haPtaData|$haPtaResult|$systemTime"
          }

          user_pta_test_result.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val recordDate = item("RECORD_DATE").asInstanceOf[Timestamp].getTime
            val location = item("LOCATION").asInstanceOf[Array[String]]
            val haPtaData = item("HA_PTA_DATA").asInstanceOf[Array[Float]]
            val haPtaResult = item("HA_PTA_RESULT").asInstanceOf[Float]
            val systemTime = item("SYSTEM_TIME").asInstanceOf[Timestamp].getTime

            UserPtaTestResult(patientId,
              recordDate,
              location.mkString(","),
              haPtaData.mkString(","),
              haPtaResult,
              systemTime).toString
          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "DIGIT_RECALL_TEST_RESULT" => {

          val digit_reacall_test_result: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "DIGIT_RECALL_TEST_RESULT",
            Seq("PATIENT_ID",
              "RECORD_DATE",
              "LOCATION",
              "HA_DIGIT_RECALL_DATA",
              "HA_DIGIT_RECALL_SCORE",
              "COMPLETED",
              "LANGUAGE",
              "START_TIME",
              "END_TIME",
              "SYSTEM_TIME"),
            zkUrl = Some("localhost:2181"))

          case class DigitReacallRestResult(
                                            patientId:String,
                                            recordDate:Long,
                                            location:String,
                                            haDigitRecallData:String,
                                            haDigitRecallScore:Int,
                                            completed:Boolean,
                                            language:String,
                                            startTime: Long,
                                            endTime: Long,
                                            systemTime:Long){
            override def toString =
              s"$patientId|$recordDate|$location|$haDigitRecallData|$haDigitRecallScore|$completed|$language|$startTime|$endTime|$systemTime"
          }


          digit_reacall_test_result.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val recordDate = item("RECORD_DATE").asInstanceOf[Timestamp].getTime
            val location = item("LOCATION").asInstanceOf[Array[String]]
            val haDigitRecallData = item("HA_DIGIT_RECALL_DATA").asInstanceOf[String]
            val haDigitRecallScore = item("HA_DIGIT_RECALL_SCORE").asInstanceOf[Int]
            val completed = item("COMPLETED").asInstanceOf[Boolean]
            val language = item("LANGUAGE").asInstanceOf[String]
            val startTime = item("START_TIME").asInstanceOf[Timestamp].getTime
            val endTime = item("END_TIME").asInstanceOf[Timestamp].getTime
            val systemTime = item("SYSTEM_TIME").asInstanceOf[Timestamp].getTime

            DigitReacallRestResult(patientId,
              recordDate,
              location.mkString(","),
              haDigitRecallData,
              haDigitRecallScore,
              completed,
              language,
              startTime,
              endTime,
              systemTime).toString
          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "SPEECH_IN_BABBLE_TEST" => {

          val speech_in_babble_test: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "SPEECH_IN_BABBLE_TEST",
            Seq("PATIENT_ID",
              "RECORD_DATE",
              "TRAINING_TASK_ID",
              "SIB_TEST_DATA",
              "TASK_COMPLETION_RATE",
              "COMPLETED",
              "NUMBER_OF_TRIALS",
              "WORDS_LIST",
              "LANGUAGE",
              "START_TIME",
              "END_TIME",
              "LOCATION",
              "SYSTEM_TIME"),
            zkUrl = Some("localhost:2181"))

          case class SpeechInBabbleTest(
                                         patientId:String,
                                         recordDate:Long,
                                         trainingTaskId:Int,
                                         sibTestData:String,
                                         taskCompletionRate:Float,
                                         completed:Boolean,
                                         numberOfTrials:Int,
                                         wordsList:String,
                                         language:String,
                                         startTime: Long,
                                         endTime: Long,
                                         location:String,
                                         systemTime:Long){
            override def toString =
              s"$patientId|$recordDate|$trainingTaskId|$sibTestData|$taskCompletionRate|$completed|$numberOfTrials|$wordsList|$language|$startTime|$endTime|$location|$systemTime"
          }

          speech_in_babble_test.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val recordDate = item("RECORD_DATE").asInstanceOf[Timestamp].getTime
            val trainingTaskId = item("TRAINING_TASK_ID").asInstanceOf[Int]
            val sibTestData = item("SIB_TEST_DATA").asInstanceOf[String]
            val taskCompletionRate = item("TASK_COMPLETION_RATE").asInstanceOf[Float]
            val completed = item("COMPLETED").asInstanceOf[Boolean]
            val numberOfTrials = item("NUMBER_OF_TRIALS").asInstanceOf[Int]
            val wordsList = item("WORDS_LIST").asInstanceOf[String]
            val language = item("LANGUAGE").asInstanceOf[String]
            val startTime = item("START_TIME").asInstanceOf[Timestamp].getTime
            val endTime = item("END_TIME").asInstanceOf[Timestamp].getTime
            val location = item("LOCATION").asInstanceOf[Array[String]]
            val systemTime = item("SYSTEM_TIME").asInstanceOf[Timestamp].getTime

            SpeechInBabbleTest(patientId,
              recordDate,
              trainingTaskId,
              sibTestData,
              taskCompletionRate,
              completed,
              numberOfTrials,
              wordsList,
              language,
              startTime,
              endTime,
              location.mkString(","),
              systemTime).toString
          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "AUDITORY_TRAINING" => {
          val auditory_training: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "AUDITORY_TRAINING",
            Seq("PATIENT_ID",
              "TASK_COMPLETION_RATE",
              "LANGUAGE",
              "START_TIME",
              "END_TIME",
              "SYSTEM_TIME"),
            zkUrl = Some("localhost:2181"))

          case class AuditoryTraining(
                                         patientId:String,
                                         language: String,
                                         startTime: Long,
                                         endTime: Long,
                                         systemTime:Long){
            override def toString =
              s"$patientId|$language|$startTime|$endTime|$systemTime"
          }

          auditory_training.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val language = item("LANGUAGE").asInstanceOf[String]
            val startTime = item("START_TIME").asInstanceOf[Timestamp].getTime
            val endTime = item("END_TIME").asInstanceOf[Timestamp].getTime
            val systemTime = item("SYSTEM_TIME").asInstanceOf[Timestamp].getTime

            AuditoryTraining(patientId,
              language,
              startTime,
              endTime,
              systemTime).toString
          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "TTSNIHL_TEST_RESULT" => {

          val ttsnihl_test_result: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "TTSNIHL_TEST_RESULT",
            Seq("PATIENT_ID",
              "RECORD_DATE",
              "TYPE",
              "LOCATION",
              "START_TIME",
              "END_TIME",
              "SYSTEM_TIME"),
            zkUrl = Some("localhost:2181"))

          case class TtsnihlTestResult(
                                             patientId:String,
                                             recordDate:Long,
                                             typ3: Int,
                                             location:String,
                                             startTime: Long,
                                             endTime: Long,
                                             systemTime:Long){
            override def toString =
              s"$patientId|$recordDate|$typ3|$location|$startTime|$endTime|$systemTime"
          }


          ttsnihl_test_result.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val recordDate = item("RECORD_DATE").asInstanceOf[Timestamp].getTime
            val typ3 = item("TYPE").asInstanceOf[Short]
            val location = item("LOCATION").asInstanceOf[Array[String]]
            val startTime = item("START_TIME").asInstanceOf[Timestamp].getTime
            val endTime = item("END_TIME").asInstanceOf[Timestamp].getTime
            val systemTime = item("SYSTEM_TIME").asInstanceOf[Timestamp].getTime

            TtsnihlTestResult(patientId,
              recordDate,
              typ3,
              location.mkString(","),
              startTime,
              endTime,
              systemTime).toString
          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "TABLE_DS11_1_OUTPUT" => {
          val table_ds11_1_output: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "TABLE_DS11_1_OUTPUT",
            Seq("PATIENT_ID",
              "FROM",
              "TO",
              "AVG_HA_USAGE",
              "TOTAL_HA_USAGE",
              "ROW_ID"),
            zkUrl = Some("localhost:2181"))

          case class TableDS111Output(
                                       patientId:String,
                                       from:Long,
                                       to:Long,
                                       avgHaUsage:Double,
                                       totalHaUsage:Double,
                                       rowId:Long){
            override def toString =
              s"$patientId|$from|$to|$avgHaUsage|$totalHaUsage|$rowId"
          }

          table_ds11_1_output.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val from = item("FROM").asInstanceOf[Long]
            val to = item("TO").asInstanceOf[Long]
            val avgHaUsage = item("AVG_HA_USAGE").asInstanceOf[Double]
            val totalHaUsage = item("TOTAL_HA_USAGE").asInstanceOf[Double]
            val rowId = item("ROW_ID").asInstanceOf[Long]

            TableDS111Output(patientId, from, to, avgHaUsage, totalHaUsage, rowId).toString

          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "TABLE_DS11_2_OUTPUT" => {
          val table_ds11_2_output: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "TABLE_DS11_2_OUTPUT",
            Seq("PATIENT_ID",
              "FROM",
              "TO",
              "AVG_HA_USAGE",
              "TOTAL_HA_USAGE",
              "ROW_ID"),
            zkUrl = Some("localhost:2181"))

          case class TableDS112Output(
                                       patientId:String,
                                       from:Long,
                                       to:Long,
                                       avgHaUsage:Double,
                                       totalHaUsage:Double,
                                       rowId:Long){
            override def toString =
              s"$patientId|$from|$to|$avgHaUsage|$totalHaUsage|$rowId"
          }

          table_ds11_2_output.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val from = item("FROM").asInstanceOf[Long]
            val to = item("TO").asInstanceOf[Long]
            val avgHaUsage = item("AVG_HA_USAGE").asInstanceOf[Double]
            val totalHaUsage = item("TOTAL_HA_USAGE").asInstanceOf[Double]
            val rowId = item("ROW_ID").asInstanceOf[Long]

            TableDS112Output(patientId, from, to, avgHaUsage, totalHaUsage, rowId).toString

          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "TABLE_DS11_3_OUTPUT" => {
          val table_ds11_3_output: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "TABLE_DS11_3_OUTPUT",
            Seq("PATIENT_ID",
              "FROM",
              "TO",
              "AVG_HA_USAGE",
              "TOTAL_HA_USAGE",
              "ROW_ID"),
            zkUrl = Some("localhost:2181"))

          case class TableDS113Output(
                                       patientId:String,
                                       from:Long,
                                       to:Long,
                                       avgHaUsage:Double,
                                       totalHaUsage:Double,
                                       rowId:Long){
            override def toString =
              s"$patientId|$from|$to|$avgHaUsage|$totalHaUsage|$rowId"
          }

          table_ds11_3_output.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val from = item("FROM").asInstanceOf[Long]
            val to = item("TO").asInstanceOf[Long]
            val avgHaUsage = item("AVG_HA_USAGE").asInstanceOf[Double]
            val totalHaUsage = item("TOTAL_HA_USAGE").asInstanceOf[Double]
            val rowId = item("ROW_ID").asInstanceOf[Long]

            TableDS113Output(patientId, from, to, avgHaUsage, totalHaUsage, rowId).toString

          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "TABLE_DS11_4_OUTPUT" => {
          val table_ds11_4_output: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "TABLE_DS11_4_OUTPUT",
            Seq("PATIENT_ID",
              "FROM",
              "TO",
              "AVG_HA_USAGE",
              "TOTAL_HA_USAGE",
              "ROW_ID"),
            zkUrl = Some("localhost:2181"))

          case class TableDS114Output(
                                       patientId:String,
                                       from:Long,
                                       to:Long,
                                       avgHaUsage:Double,
                                       totalHaUsage:Double,
                                       rowId:Long){
            override def toString =
              s"$patientId|$from|$to|$avgHaUsage|$totalHaUsage|$rowId"
          }

          table_ds11_4_output.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val from = item("FROM").asInstanceOf[Long]
            val to = item("TO").asInstanceOf[Long]
            val avgHaUsage = item("AVG_HA_USAGE").asInstanceOf[Double]
            val totalHaUsage = item("TOTAL_HA_USAGE").asInstanceOf[Double]
            val rowId = item("ROW_ID").asInstanceOf[Long]

            TableDS114Output(patientId, from, to, avgHaUsage, totalHaUsage, rowId).toString

          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case "TABLE_DS11_5_OUTPUT" => {
          val table_ds11_5_output: RDD[Map[String, AnyRef]] = sc.phoenixTableAsRDD(
            "TABLE_DS11_5_OUTPUT",
            Seq("PATIENT_ID",
              "FROM",
              "TO",
              "AVG_HA_USAGE",
              "TOTAL_HA_USAGE",
              "ROW_ID"),
            zkUrl = Some("localhost:2181"))

          case class TableDS115Output(
                                       patientId:String,
                                       from:Long,
                                       to:Long,
                                       avgHaUsage:Double,
                                       totalHaUsage:Double,
                                       rowId:Long){
            override def toString =
              s"$patientId|$from|$to|$avgHaUsage|$totalHaUsage|$rowId"
          }

          table_ds11_5_output.map(item => {
            val patientId = item("PATIENT_ID").asInstanceOf[String]
            val from = item("FROM").asInstanceOf[Long]
            val to = item("TO").asInstanceOf[Long]
            val avgHaUsage = item("AVG_HA_USAGE").asInstanceOf[Double]
            val totalHaUsage = item("TOTAL_HA_USAGE").asInstanceOf[Double]
            val rowId = item("ROW_ID").asInstanceOf[Long]

            TableDS115Output(patientId, from, to, avgHaUsage, totalHaUsage, rowId).toString

          }).saveAsTextFile("hdfs://10.207.1.102:54310/evotion_data/" + table.toLowerCase)
        }
        case _ => println(s"Table $table is not available for export. Please try a different name.")
      }
    })
    sc.stop
  }
}