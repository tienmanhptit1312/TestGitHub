import org.apache.spark.SparkContext
import org.apache.spark.ml.feature.BucketedRandomProjectionLSH
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{SQLContext, SparkSession}

/**
  * Created by manh on 15/12/2016.
  */
object LSHTest {
  def main(args: Array[String]): Unit = {
    // Creates a SparkSession
//    val spark = SparkSession
//      .builder
//      .appName("BucketedRandomProjectionLSHExample")
//      .getOrCreate()
    val sc=new SparkContext()
    val spark=new SQLContext(sc)
    import spark.implicits._
    // $example on$
    val dfA = spark.createDataFrame(Seq(
      (0, Vectors.dense(1.0, 1.0)),
      (1, Vectors.dense(1.0, -1.0)),
      (2, Vectors.dense(-1.0, -1.0)),
      (3, Vectors.dense(-1.0, 1.0))
    )).toDF("id", "keys")

    val dfB = spark.createDataFrame(Seq(
      (4, Vectors.dense(1.0, 0.0)),
      (5, Vectors.dense(-1.0, 0.0)),
      (6, Vectors.dense(0.0, 1.0)),
      (7, Vectors.dense(0.0, -1.0))
    )).toDF("id", "keys")

    val key = Vectors.dense(1.0, 0.0)

    val brp = new BucketedRandomProjectionLSH()
      .setBucketLength(2.0)
      .setNumHashTables(3)
      .setInputCol("keys")
      .setOutputCol("values")

    val model = brp.fit(dfA)

    // Feature Transformation
    model.transform(dfA).show()
    // Cache the transformed columns
    val transformedA = model.transform(dfA).cache()
    val transformedB = model.transform(dfB).cache()

    // Approximate similarity join
    model.approxSimilarityJoin(dfA, dfB, 1.5).show()
    model.approxSimilarityJoin(transformedA, transformedB, 1.5).show()
    // Self Join
    model.approxSimilarityJoin(dfA, dfA, 2.5).filter("datasetA.id < datasetB.id").show()

    // Approximate nearest neighbor search
    model.approxNearestNeighbors(dfA, key, 2).show()
    model.approxNearestNeighbors(transformedA, key, 2).show()
    // $example off$


  }

}
