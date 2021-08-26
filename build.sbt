name := "wqsub-spark"
version := "1.0"

scalaVersion := "2.12.10"

val sparkVersion            = "3.1.1"
val wikidataToolkitVersion  = "0.12.1"
val jacksonVersion          = "2.10.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion,

  "org.wikidata.wdtk" % "wdtk-dumpfiles"   % wikidataToolkitVersion,
  "org.wikidata.wdtk" % "wdtk-wikibaseapi" % wikidataToolkitVersion,
  "org.wikidata.wdtk" % "wdtk-datamodel"   % wikidataToolkitVersion,
  "org.wikidata.wdtk" % "wdtk-rdf"         % wikidataToolkitVersion,
  "org.wikidata.wdtk" % "wdtk-storage"     % wikidataToolkitVersion,
  "org.wikidata.wdtk" % "wdtk-util"        % wikidataToolkitVersion,

  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.12.2",

  "org.rogach" %% "scallop" % "4.0.4"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

lazy val app = (project in file("app"))
  .settings(
    assembly / mainClass := Some("es.weso.wqsub.MeanOutDegreeFast"),
  )