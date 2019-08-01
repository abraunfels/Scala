name := "Practice"

version := "0.1"

scalaVersion := "2.12.8"

//resolvers ++= Seq("Sonatype OSS" at "https://oss.sonatype.org/content/groups/public")

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.23"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.13"
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.3"
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "2.6.0"
libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.9"
libraryDependencies += "ai.snips" %% "play-mongo-bson" % "0.5"