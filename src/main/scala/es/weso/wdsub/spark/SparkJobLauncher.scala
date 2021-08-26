package es.weso.wdsub.spark

// spark-submit --master local[*] --class es.weso.wqsub.MeanOutDegreeFast /gfs/home/thewillyhuman/labra-wsub/spark-jobs/wqsub-spark/wqsub-spark-assembly-1.0.jar cluster /gfs/projects/weso-scholia/dumps/latest-all.json
object SparkJobLauncher
{
  def main(args: Array[String]): Unit =
  {
    val sparkJobConf = new SparkJobConfig( args )
    new SparkJobDefinition( sparkJobConf )
  }
}