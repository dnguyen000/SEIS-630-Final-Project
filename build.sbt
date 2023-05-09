import sbt.Keys.resolvers

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"
//ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "SEIS-630-Final-Project",
    libraryDependencies += "io.spray" %% "spray-json" % "1.3.6",
    libraryDependencies += "com.jayway.jsonpath" % "json-path" % "2.8.0"
  )
