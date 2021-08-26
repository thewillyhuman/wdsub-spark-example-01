package es.weso.wdsub.spark

import org.rogach.scallop._

class SparkJobConfig(arguments: Seq[String]) extends ScallopConf(arguments) {
//jobMode: SparkJobDefinitionMode, jobName: String, jobInputFile: String, jobOutputDir: String

  banner(
    """Welcome to WDSub Spark Launcher!!!
      |To launch any job please provide the following parameters""".stripMargin)

  val jobMode = opt[String](required = true, name = "mode", descr = "Select the mode to run: (test|cluster)")
  val jobName = opt[String](required = true, name = "name", descr = "Enter a name for the job")
  val jobInputFile = opt[String](required = true, name = "in", descr = "Select the input file by entering the path")
  val jobOutputDir = opt[String](required = true, name = "out", descr = "Select the output directory. Please remove the trailing slash")

  printHelp()
  verify()
}
