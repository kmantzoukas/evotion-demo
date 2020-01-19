name := "export-hbase-tables"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.7"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.1.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.1.1"
libraryDependencies += "org.apache.phoenix" % "phoenix-spark" % "4.8.0-HBase-1.0"