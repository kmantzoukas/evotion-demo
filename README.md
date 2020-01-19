[![N|Solid](https://evotion.city.ac.uk/images/evotion-logo.png)]

This repository contains the following modules:
  - __Data Extraction Application__ - A Spark application that allows for the extraction of data from HBASE to HDFS for further processing
  - __Big Data Analytics Services__ - A set of Spark applications that represent the Big Data analytics that are submitted for execution on Spark
  - __REST API Services__ - A REST API to invoke the execution of Spark jobs for the production of the relevant analytics

# Data Extraction Application
___
The data extraction is implemented as a Spark application that loads the data from HBASE and stores it on HDFS. The data is then used for the generation of the Big Data analytics.

### Build
To build the Spark application run the following commands:

```sh
$ cd export-hbase-tables
$ sbt clean package
```
Once the packaging of the application has been successfully completed, upload the __jar__ file that has been produced in the __target/scala-2.11__ directory under the name *__export-hbase-tables_2.11-0.0.1-SNAPSHOT.jar__* on the evotion server with IP address __10.207.24.20__.

### Execute
To execute the data extraction services, login to the evotion server with IP address __10.207.24.20__ and navigate to the location where the __jar__ file produced at the build stage above has been uploaded. Then run the following command:

```sh
$ /usr/hdp/2.6.2.0-205/spark2/bin/spark-submit \
--master local[*] \
--class ExportHBaseTableService \
--driver-memory 2g \
--executor-memory 1g \
--conf "spark.driver.extraClassPath=/usr/hdp/2.6.2.0-205/phoenix/phoenix-spark2.jar:/usr/hdp/2.6.2.0-205/phoenix/phoenix-client.jar:/usr/hdp/2.6.2.0-205/hbase/conf" \
--conf "spark.executor.extraClassPath=/usr/hdp/2.6.2.0-205/phoenix/phoenix-spark2.jar:/usr/hdp/2.6.2.0-205/phoenix/phoenix-client.jar:/usr/hdp/2.6.2.0-205/hbase/conf" \
/path/to/export-hbase-tables_2.11-0.0.1-SNAPSHOT.jar \
HA_ENVRIRONMENT_DATA USER_PTA_TEST_RESULT DIGIT_RECALL_TEST_RESULT SPEECH_IN_BABBLE_TEST AUDITORY_TRAINING TABLE_DS11_1_OUTPUT TABLE_DS11_2_OUTPUT TABLE_DS11_3_OUTPUT TABLE_DS11_4_OUTPUT TABLE_DS11_5_OUTPUT
```

>Note that the list of tables to be extracted is provided as a list of arguments to the service. This is a convenient way for extracting data only from the tables that are needed. Also note that a set of dependenices need to be specified for the driver and the executors to handle the communication with HBASE. It is critical to run this application on the evotion server where all the relevant dependencies are present. Finally, the dependencies used are for __Apache Spark 2.x__ and therefore the appropriate path is used, in which case that is __/usr/hdp/2.6.2.0-205/spark2/bin/spark-submit__ 

# Big Data Analytics
___
The Big Data analytics are a set of Spark applications that process the data an produce a series of analytics. The submission of the Spark jobs that they represent are submitted on Spark through Spark's REST API. Spark's REST API allows for the submission and the inspection of the status of job as soon as it has been submitted through a series of HTTP requests.

### Build
To build the Big Data services navigate to the __spark-services__ folder and then into each subfolder that represents a separate service. Then use __sbt__ to compile and package each service into a separate __jar__ file. To achieve this execute the following commands:

```sh
$ cd spark-services/auditory-training-analytics
$ sbt clean package
```
```sh
$ cd spark-services/digit-recall-test-analytics
$ sbt clean package
```
```sh
$ cd spark-services/usage-analytics
$ sbt clean package
```
```sh
$ cd spark-services/user-pta-test-result-analytics
$ sbt clean package
```
```sh
$ cd spark-services/speech-in-babble-test-analytics
$ sbt clean package
```

When all the project have been built successfully, under the __target/scala-2.11__ sub-folder of each project, a __jar__ file will be created with the same name as the project name i.e. the folder name and the ___2.11-0.0.1-SNAPSHOT__ suffix appended at the end indicating that __Scala 2.11__ has been used for the compilation. All this metadata can be inspected in the build.sbt of each project.
#### Execute
In our use case, the Big Data analytics services are not executed manually but through Spark's REST API. The submission of the jobs is triggered by means of invoking the REST API that is described in the next section.

# REST API Services
The REST API is used as an interface to interact with the Spark cluster. It is used from the Evotion dashboard to initiate the execution of the Big Data analytics jobs and present the results to the users.

### Build
To build the REST API project execute the following commands:
```sh
$ cd evotion-rest-api
$ mvn clean package
```
### Execute
To execute the REST API and make it expose its services execute the following command while being in the evotion-rest-api folder:
```sh
$ mvn spring-boot:run
```

A comprehensive list of all the services and parameters required is layed out in the list below.

##### Auditory Training Analytics
 - __Get analytics for all patients__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/auditory-training
 - __Get analytics for all patients where language is *el* or *en*__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/auditory-training?lang=en
 - __Get analytics for a comma-separated list of patient ids__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/auditory-training?ids=IN146523,IN150702,IN161564

##### Digit Recall Test Analytics
 - __Get analytics for all patients__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/digit-recall-test
 - __Get analytics for all patients where language is *el* or *en*__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/digit-recall-test?lang=en
 - __Get analytics for a comma-separated list of patient ids__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/digit-recall-test?ids=IN146523,IN150702,IN161564

##### Usage Analytics
 - __Get analytics for all patients for a specific period. Valid options are *PER_HOUR, PER_6HOURS, PER_DAY, PER_WEEK, PER_MONTH*__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/usage?period=PER_MONTH
 - __Get analytics for a specific period and for a comma-separated list of patient ids__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/usage?period=PER_MONTH&ids=IN146688,IN146410,IN146409

##### Speech In Babble Analytics
 - __Get analytics for all patients__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/speech-in-babble-test
 - __Get analytics for all patients where language is *el* or *en*__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/speech-in-babble-test?lang=en
 - __Get analytics for a comma-separated list of patient ids__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/speech-in-babble-test?ids=IN146523,IN150702,IN161564

##### User PTA Test Result Analytics
 - __Get analytics for all patients__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/user-pta-test-results
 - __Get analytics for a specific period and for a comma-separated list of patient ids__
 e.g. http://soi-vm-test1.nsqdc.city.ac.uk/:8090/evotion/rest/api/analytics/user-pta-test-results&ids=IN146688,IN146410,IN146409

> Note that the REST API is deployed on the soi-vm-test1.nsqdc.city.ac.uk server and therefore it is essential to be logged in to the City VPN. Finally, the REST API can be deployed on any computer that has access to the soi-vm-test1.nsqdc.city.ac.uk machine. This is due to the fact that it was designed with that in mind to allow service execution from any location, even outside City's servers.
