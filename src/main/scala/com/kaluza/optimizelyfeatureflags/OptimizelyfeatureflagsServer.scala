package com.kaluza.optimizelyfeatureflags

import cats.effect.{ConcurrentEffect, Timer}
import cats.implicits._
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import scala.concurrent.ExecutionContext.global

object OptimizelyfeatureflagsServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F]): Stream[F, Nothing] = {
    val sdkKey =
      sys.env.getOrElse("OPTIMIZELY_SDK_KEY", throw new Exception("Where's OPTIMIZELY_SDK_KEY?"))

    for {
      client <- BlazeClientBuilder[F](global).stream
      reader <- FeatureFlagReader(sdkKey)
      helloWorldAlg = HelloWorld.impl[F](reader)
      jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        OptimizelyfeatureflagsRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        OptimizelyfeatureflagsRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}
