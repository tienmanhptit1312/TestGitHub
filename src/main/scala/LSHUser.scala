import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLContext, SparkSession}
import org.apache.spark.ml.feature.BucketedRandomProjectionLSH
import org.apache.spark.ml.linalg.{SparseVector, Vectors}
import org.apache.spark.sql.functions._
import org.apache.spark.mllib.rdd.MLPairRDDFunctions._
//import scala.collection.immutable.Map
import scala.collection.Map
import scala.reflect.ClassTag

/**
  * Created by manh on 16/12/2016.
  */
object LSHUser {
  implicit def kryoEncoder[A](implicit ct: ClassTag[A]) =
    org.apache.spark.sql.Encoders.kryo[A](ct)
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext()
    val sqlContext = new SQLContext(sc)
//    val sparkSession=new SparkSession()
//    import sparkSession.implicits._
//    implicit val myObjEncoder = org.apache.spark.sql.Encoders.kryo[User]


    import sqlContext.implicits._
    val df = sqlContext.read.parquet("hdfs://192.168.23.130:9000/data/DMP/PC-Data/StaticData/UserProfile").as[User]
    //    val user=df.map(f=>{
    //      val guid=f(0).asInstanceOf[Long]
    //      val interest=f(1).asInstanceOf[Map[Int,Double]]
    //      val sparse=Vectors.sparse(230,interest.toSeq)
    //      new Tuple2[Long,SparseVector](guid,sparse.toSparse)
    //    }).toDF("guid","interest")
    val user = df.map(user => {
      val guid = user.guid
      val topicsInterest = user.topicsInterest
      val vector = Vectors.sparse(230, topicsInterest.toSeq)
      new Tuple2[Long, SparseVector](guid, vector.toSparse)
    }).toDF("guid", "interest")
    val brp = new BucketedRandomProjectionLSH()
      .setBucketLength(40)
      .setNumHashTables(3)
      .setInputCol("interest")
      .setOutputCol("values")

      val model=brp.fit(user)
    //    model.getNumHashTables
    //    val transform=model.transform(user)
    //    println(model.getNumHashTables)
    //    val key=Vectors.sparse(230,Array(11,17,8,10),Array(0.5,0.6,-2.555,3.9))
    //    model.approxNearestNeighbors(user,key,4).show()
    //    model.transform(user).take(10).foreach(f=>println(f.apply(2)))
    //    model.approxSimilarityJoin(transform,transform,10).show()

    val seedUser = df.map(user=>{
      val guid=user.guid
      val topic=user.topicsInterest
      new Tuple2[Long,Map[Int,Double]](guid,topic)
    }).filter(f=>f._2.size<4)

    //    val listUser=seedUser.map(._interest)
    var listUser=List()

    val data = model.approxSimilarityJoin(user, user, 2.5)
      .select(col("datasetA.guid"), col("datasetB.guid") as "id2", col("distCol"))
    data.printSchema()
    val topK = data.map(r => {
      (r.getAs[Int]("guid"), (r.getAs[Int]("id2"), r.getAs[Double]("distCol")))
    }).rdd.topByKey(5)(Ordering.by(-_._2)).repartition(20)
    topK.count();
//    val data =seedUser.map(f => {
//      var list = List()
//      val key = Vectors.sparse(230,f._2.toSeq)
//      val guid = model.approxNearestNeighbors(user, key, 10).select("guid").show()
////      listUser.:::(list)
//
////      println(list.size)
////      return key
//      key
//    })
//    val samples = data.take(5);
//    println(samples.mkString("\n"))
////    println(listUser.size)
  }
}

case class User(guid: Long, topicsInterest: Map[Int, Double], gender: Int, age: Int) {}
case class SeedUser(guid:Long,topicsInterest:Map[Int,Double]){}