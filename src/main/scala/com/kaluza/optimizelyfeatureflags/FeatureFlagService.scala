package com.kaluza.optimizelyfeatureflags

import java.util.concurrent.TimeUnit

import cats.effect.Sync
import com.optimizely.ab.Optimizely
import com.optimizely.ab.config.{HttpProjectConfigManager, ProjectConfigManager}
import fs2.Stream

object FeatureFlagService {
  def apply[F[_]: Sync](sdkKey: String): Stream[F, Optimizely] = {
    val projectConfigManager: ProjectConfigManager = HttpProjectConfigManager
      .builder()
      .withSdkKey(sdkKey)
      .withPollingInterval(5, TimeUnit.SECONDS)
      .build()

    Stream.eval {
      Sync[F].delay(
        Optimizely
          .builder()
          .withConfigManager(projectConfigManager)
          .build()
      )
    }

  }
}
