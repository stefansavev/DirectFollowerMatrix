name := "LANA Direct Follower Matrix"

version := "0.2"

scalaVersion := "2.12.8"

resolvers += "Typesafe Repository".at(
  "http://repo.typesafe.com/typesafe/releases/")

resolvers += "MavenRepository".at("https://mvnrepository.com/")

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-csv" % "1.8",
  // testing
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
