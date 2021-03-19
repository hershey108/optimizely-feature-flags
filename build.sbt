val Http4sVersion          = "0.21.16"
val CirceVersion           = "0.13.0"
val MunitVersion           = "0.7.20"
val LogbackVersion         = "1.2.3"
val MunitCatsEffectVersion = "0.13.0"

val OptimizelyCoreVersion = "3.8.0"
val JacksonVersion        = "2.9.8"

resolvers += Resolver.bintrayRepo("optimizely", "optimizely")

fork in run := true

lazy val root = (project in file("."))
  .settings(
    organization := "com.kaluza",
    name := "optimizely-feature-flags",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.13.4",
    libraryDependencies ++= Seq(
      "org.typelevel"             %% "cats-effect"          % "2.3.3",
      "org.http4s"                %% "http4s-blaze-server"  % Http4sVersion,
      "org.http4s"                %% "http4s-blaze-client"  % Http4sVersion,
      "org.http4s"                %% "http4s-circe"         % Http4sVersion,
      "org.http4s"                %% "http4s-dsl"           % Http4sVersion,
      "io.circe"                  %% "circe-generic"        % CirceVersion,
      "org.scalameta"             %% "munit"                % MunitVersion           % Test,
      "org.typelevel"             %% "munit-cats-effect-2"  % MunitCatsEffectVersion % Test,
      "ch.qos.logback"             % "logback-classic"      % LogbackVersion,
      "com.optimizely.ab"          % "core-api"             % OptimizelyCoreVersion,
      "com.optimizely.ab"          % "core-httpclient-impl" % OptimizelyCoreVersion,
      "com.fasterxml.jackson.core" % "jackson-core"         % JacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-annotations"  % JacksonVersion,
      "com.fasterxml.jackson.core" % "jackson-databind"     % JacksonVersion
    ),
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    testFrameworks += new TestFramework("munit.Framework")
  )
