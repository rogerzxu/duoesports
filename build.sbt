name := "loliga"

version := "1.0"

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % "4.1.0",
  guice,
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "org.webjars" %% "webjars-play" % "2.6.2",
  "org.webjars" % "bootstrap" % "3.3.7",
  "org.webjars" % "jquery" % "3.2.1"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
