name := "loliga"

version := "1.0"

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  "net.codingwell" %% "scala-guice" % "4.1.0",
  guice,
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",
  "org.webjars" %% "webjars-play" % "2.6.2",
  "org.webjars" % "bootstrap" % "3.3.7",
  "org.webjars" % "jquery" % "3.2.1",
  "org.webjars" % "vue" % "2.5.3",
  "org.webjars.npm" % "vuelidate" % "0.5.0",
  "com.iheart" %% "ficus" % "1.4.1",
  "com.typesafe.scala-logging" % "scala-logging_2.12" % "3.7.2"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
