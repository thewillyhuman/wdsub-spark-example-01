package es.weso.wdsub.spark

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.wikidata.wdtk.datamodel.helpers.JsonDeserializer
import org.wikidata.wdtk.datamodel.implementation.ItemDocumentImpl

import java.text.SimpleDateFormat
import java.util.Calendar

class SparkJobDefinition(sparkJobConfig: SparkJobConfig) {

  // Create the result file for later use.
  val resultFile = new ResultFile()
  val date = Calendar.getInstance().getTime();
  val dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
  val strDate = dateFormat.format(date);
  resultFile.jobName = sparkJobConfig.jobName.apply()
  resultFile.jobDate = strDate

  // Create the spark context that will be initialized depending on the job mode.
  var sparkContext: SparkContext = null
  if( SparkJobDefinitionMode.fromString(sparkJobConfig.jobMode.apply()).equals(SparkJobDefinitionMode.Test) ) {
    sparkContext = SparkSession.builder().master("local[*]").appName(sparkJobConfig.jobName.apply()).getOrCreate().sparkContext
  } else if( SparkJobDefinitionMode.fromString(sparkJobConfig.jobMode.apply()).equals(SparkJobDefinitionMode.Cluster) ) {
    sparkContext = new SparkContext( new SparkConf().setAppName( sparkJobConfig.jobName.apply()) )
  }

  // Start measuring the execution time.
  val jobStartTime = System.nanoTime

  // Load the dump in to an RDD of type String. The RDD will be composed of each single line as a String.
  val dumpLines: RDD[String] = sparkContext.textFile( sparkJobConfig.jobInputFile.apply() )

  // Compute the out degree of each line. If the line is not an item then the line is discarded.
  val outDegrees: RDD[(Int, Int)] = dumpLines.map( line => {

    var outDegree = (0, 0)

    if( line.contentEquals("]") || line.contentEquals("[") ) {
      // Skip line
    } else {
      val jsonDeserializer = new JsonDeserializer( "http://www.wikidata.org/entity/" )
      val entityDocument = jsonDeserializer.deserializeEntityDocument( line )

      if( entityDocument.isInstanceOf[ItemDocumentImpl] ) {
        val itemDocumentImpl = entityDocument.asInstanceOf[ItemDocumentImpl]
        outDegree = (itemDocumentImpl.getStatementGroups.size(), 1)
      }
    }
    outDegree
  })

  // Reduce the result to a single one tuple where we will have the sum of all the out degers and the number of items.
  val (sum, count) = outDegrees.reduce((x, y) => (x._1 + y._1, x._2 + y._2))

  // Compute the average out degree. It is computed as the addition of all the degrees over the total number
  // of items in the dataset.
  val avgOutDegree: Double = sum.toLong.asInstanceOf[Double] / count.toLong.asInstanceOf[Double]

  // Get the job execution time in seconds.
  val jobExecutionTime = (System.nanoTime - jobStartTime) / 1e9d
  val jobCores = java.lang.Runtime.getRuntime.availableProcessors * ( sparkContext.statusTracker.getExecutorInfos.length -1 )
  val jobMem = java.lang.Runtime.getRuntime.totalMemory() * ( sparkContext.statusTracker.getExecutorInfos.length -1 )

  resultFile.time = jobExecutionTime.toString
  resultFile.cores = jobCores.toString
  resultFile.mem = jobMem.toString

  val resultString = new StringBuilder()
  resultString.append( s"Total Out Degree -> $sum\n" )
  resultString.append( s"Total Number of Items -> $count\n" )
  resultString.append( s"Average Out Degree -> $avgOutDegree" )

  resultFile.jobResults = resultString.toString()
  sparkContext.parallelize(Seq(resultFile.toString()), 1)
    .saveAsTextFile(s"${sparkJobConfig.jobOutputDir.apply()}/${sparkContext.applicationId}_${sparkJobConfig.jobName.apply()}_out")
}
