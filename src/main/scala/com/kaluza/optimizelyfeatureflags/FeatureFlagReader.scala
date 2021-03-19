package com.kaluza.optimizelyfeatureflags

import java.util.concurrent.TimeUnit

import cats.effect.Sync
import com.optimizely.ab.Optimizely
import com.optimizely.ab.config.HttpProjectConfigManager
import com.optimizely.ab.config.ProjectConfigManager
import com.optimizely.ab.event.AsyncEventHandler
import com.optimizely.ab.event.BatchEventProcessor
import com.optimizely.ab.event.EventHandler
import com.optimizely.ab.event.EventProcessor

trait Feature {
  val name: String
}

trait FeatureVariable {
  val feature: Feature
  val name: String
}

trait BooleanFeatureVariable extends FeatureVariable
trait DoubleFeatureVariable  extends FeatureVariable
trait IntegerFeatureVariable extends FeatureVariable
trait StringFeatureVariable  extends FeatureVariable
trait JsonFeatureVariable    extends FeatureVariable

object FeatureFlagReader {

  val DEFAULT_USER_ID = "someone_anonymous"
  val sdkKey =
    sys.env.getOrElse("OPTIMIZELY_SDK_KEY", throw new Exception("Where's OPTIMIZELY_SDK_KEY?"))

  val eventHandler: EventHandler = AsyncEventHandler
    .builder()
    .withQueueCapacity(20000)
    .withNumWorkers(5)
    .build()

  val eventProcessor: EventProcessor = BatchEventProcessor
    .builder()
    .withEventHandler(eventHandler)
    .build()

  val projectConfigManager: ProjectConfigManager = HttpProjectConfigManager
    .builder()
    .withSdkKey(sdkKey)
    .withPollingInterval(5, TimeUnit.SECONDS)
    .build()

  val client: Optimizely = Optimizely
    .builder()
    .withConfigManager(projectConfigManager)
    .withEventProcessor(eventProcessor)
    .build()

  def readFlag[F[_]: Sync](flag: Feature, userId: String): F[Boolean] = {
    Sync[F].delay(client.isFeatureEnabled(flag.name, userId))
  }

  def readFlag[F[_]: Sync](flag: Feature): F[Boolean] = readFlag(flag, DEFAULT_USER_ID)

  def readBooleanFeatureVariable[F[_]: Sync](flag: BooleanFeatureVariable, userId: String): F[Boolean] =
    Sync[F].delay(client.getFeatureVariableBoolean(flag.feature.name, flag.name, userId))

  def readBooleanFeatureVariable[F[_]: Sync](flag: BooleanFeatureVariable): F[Boolean] =
    readBooleanFeatureVariable(flag, DEFAULT_USER_ID)

  def readDoubleFeatureVariable[F[_]: Sync](flag: DoubleFeatureVariable, userId: String): F[Double] =
    Sync[F].delay(client.getFeatureVariableDouble(flag.feature.name, flag.name, userId))

  def readDoubleFeatureVariable[F[_]: Sync](flag: DoubleFeatureVariable): F[Double] =
    readDoubleFeatureVariable(flag, DEFAULT_USER_ID)

  def readIntegerFeatureVariable[F[_]: Sync](flag: IntegerFeatureVariable, userId: String): F[Integer] =
    Sync[F].delay(client.getFeatureVariableInteger(flag.feature.name, flag.name, userId))

  def readIntegerFeatureVariable[F[_]: Sync](flag: IntegerFeatureVariable): F[Integer] =
    readIntegerFeatureVariable(flag, DEFAULT_USER_ID)

  def readStringFeatureVariable[F[_]: Sync](flag: StringFeatureVariable, userId: String): F[String] =
    Sync[F].delay(client.getFeatureVariableString(flag.feature.name, flag.name, userId))

  def readStringFeatureVariable[F[_]: Sync](flag: StringFeatureVariable): F[String] =
    readStringFeatureVariable(flag, DEFAULT_USER_ID)

}
