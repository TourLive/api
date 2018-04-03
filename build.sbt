name := """api"""

version := "0.0.2"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

dockerUsername := Some("tourlive")

libraryDependencies += guice
libraryDependencies += javaJpa
libraryDependencies += "org.hibernate" % "hibernate-core" % "5.2.15.Final"
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.4"
libraryDependencies ++= Seq(
  ws
)

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

jacocoReportSettings := JacocoReportSettings()
  .withTitle("JACOCO Report")
  .withFormats(JacocoReportFormats.ScalaHTML)

jacocoExcludes := Seq("views*", "*Routes*")
jacocoDirectory := baseDirectory.value /"target/jacoco"

PlayKeys.externalizeResources := false

