package com.kaluza.optimizelyfeatureflags

import java.util.concurrent.TimeUnit

import com.optimizely.ab.Optimizely
import com.optimizely.ab.config.{HttpProjectConfigManager, ProjectConfigManager}
import com.optimizely.ab.event.{AsyncEventHandler, BatchEventProcessor, EventHandler, EventProcessor}

trait Feature {
  val name: String
}

trait FeatureVariable {
  val feature: Feature
  val name: String
}

trait BooleanFeatureVariable extends FeatureVariable
trait DoubleFeatureVariable extends FeatureVariable
trait IntegerFeatureVariable extends FeatureVariable
trait StringFeatureVariable extends FeatureVariable
trait JsonFeatureVariable extends FeatureVariable

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

  def readFlag(flag: Feature, userId: String): Boolean = {
    val result = client.isFeatureEnabled(flag.name, userId)
    result
  }

  def readFlag(flag: Feature): Boolean = readFlag(flag, DEFAULT_USER_ID)

  def readBooleanFeatureVariable(flag: BooleanFeatureVariable, userId: String): Boolean =
    client.getFeatureVariableBoolean(flag.feature.name, flag.name, userId)

  def readBooleanFeatureVariable(flag: BooleanFeatureVariable): Boolean =
    readBooleanFeatureVariable(flag, DEFAULT_USER_ID)

  def readDoubleFeatureVariable(flag: DoubleFeatureVariable, userId: String): Double =
    client.getFeatureVariableDouble(flag.feature.name, flag.name, userId)

  def readDoubleFeatureVariable(flag: DoubleFeatureVariable): Double =
    readDoubleFeatureVariable(flag, DEFAULT_USER_ID)

  def readIntegerFeatureVariable(flag: IntegerFeatureVariable, userId: String): Integer =
    client.getFeatureVariableInteger(flag.feature.name, flag.name, userId)

  def readIntegerFeatureVariable(flag: IntegerFeatureVariable): Integer =
    readIntegerFeatureVariable(flag, DEFAULT_USER_ID)

  def readStringFeatureVariable(flag: StringFeatureVariable, userId: String): String =
    client.getFeatureVariableString(flag.feature.name, flag.name, userId)

  def readStringFeatureVariable(flag: StringFeatureVariable): String =
    readStringFeatureVariable(flag, DEFAULT_USER_ID)

}
