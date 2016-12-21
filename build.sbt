
import _root_.sbtassembly.Plugin.AssemblyKeys
import _root_.sbtassembly.Plugin.AssemblyKeys
import _root_.sbtassembly.Plugin._
import sbtassembly.Plugin._
import AssemblyKeys._
assemblySettings
jarName in assembly := "AudienceExtension `.jar"
scalaVersion:="2.11.8"
libraryDependencies ++= Seq(
  //"com.vcc.bigdata" %"logprs" % "2.0.6.4",
  "redis.clients" % "jedis" % "2.6.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "1.4.0" % "provided",
  "org.apache.spark" %% "spark-sql" % "2.0.0" % "provided",
  "org.apache.spark" %% "spark-mllib" % "2.0.0" % "provided",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test",
  ("org.apache.spark" %% "spark-core" % "2.0.0" % "provided").
    exclude("commons-logging", "commons-logging").
    exclude("org.mortbay.jetty", "servlet-api").
    exclude("commons-beanutils", "commons-beanutils-core").
    exclude("commons-collections", "commons-collections").
    exclude("commons-logging", "commons-logging").
    exclude("com.esotericsoftware.minlog", "minlog").
    exclude("com.codahale.metric" , "metrics-core"),
  ("org.apache.hbase" % "hbase-client" % "1.1.0.1").
    exclude("org.apache.htrace", "htrace-core"),
  ("org.apache.hbase" % "hbase-common" % "1.1.0.1").
    exclude("org.apache.htrace", "htrace-core"),
  //  "org.apache.spark" %% "spark-streaming" % "1.3.1" % "provided",
  //  ("org.apache.spark" %% "spark-streaming-kafka" % "1.3.1").
  //    exclude("org.spark-project.spark", "unused").
  //    exclude("commons-beanutils", "commons-beanutils").
  //    exclude("commons-collections", "commons-collections").
  //    exclude("com.esotericsoftware.minlog", "minlog").
  //    exclude("com.yammer.metrics", "metrics-core"),
  "mysql" % "mysql-connector-java" % "5.1.24"
)

