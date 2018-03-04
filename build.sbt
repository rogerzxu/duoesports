name := "duoesports"

version := "1.0"

scalaVersion := "2.12.4"

resolvers += Resolver.jcenterRepo

libraryDependencies ++= Seq(
  jdbc,
  evolutions,
  ehcache,
  "com.typesafe.play" % "anorm_2.12" % "2.5.3",
  "org.mariadb.jdbc" % "mariadb-java-client" % "2.2.0",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  guice,
  "com.typesafe.play" %% "play-mailer" % "6.0.1",
  "com.typesafe.play" %% "play-mailer-guice" % "6.0.1",
  "com.mohiva" %% "play-silhouette" % "5.0.0",
  "com.mohiva" %% "play-silhouette-password-bcrypt" % "5.0.0",
  "com.mohiva" %% "play-silhouette-persistence" % "5.0.0",
  "com.mohiva" %% "play-silhouette-crypto-jca" % "5.0.0",

  "org.webjars" %% "webjars-play" % "2.6.2",
  "org.webjars" % "bootstrap-select" % "1.12.4",
  "org.webjars" % "popper.js" % "1.12.9",
  "org.webjars" % "bootstrap" % "3.3.7",
  "org.webjars" % "jquery" % "3.2.1",
  "org.webjars" % "vue" % "2.5.3",
  "org.webjars.npm" % "vue-resource" % "1.3.4",
  "org.webjars.npm" % "vuelidate" % "0.5.0",
  "com.iheart" %% "ficus" % "1.4.1",
  "com.typesafe.scala-logging" % "scala-logging_2.12" % "3.7.2",

  "com.mohiva" %% "play-silhouette-testkit" % "5.0.0" % "test",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"

)

lazy val root = (project in file(".")).enablePlugins(PlayScala)
