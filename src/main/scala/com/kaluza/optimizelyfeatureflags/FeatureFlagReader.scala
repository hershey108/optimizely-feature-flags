package com.kaluza.optimizelyfeatureflags

import cats.effect.Sync
import com.optimizely.ab.Optimizely
import fs2.Stream

case class Feature(name: String)

sealed trait FeatureVariable {
  val feature: Feature
  val name: String
}

case class BooleanFeatureVariable(feature: Feature, name: String) extends FeatureVariable
case class DoubleFeatureVariable(feature: Feature, name: String)  extends FeatureVariable
case class IntegerFeatureVariable(feature: Feature, name: String) extends FeatureVariable
case class StringFeatureVariable(feature: Feature, name: String)  extends FeatureVariable
case class JsonFeatureVariable(feature: Feature, name: String)    extends FeatureVariable

class FeatureFlagReader[F[_]: Sync](client: Optimizely) {

  private val DEFAULT_USER_ID = "someone_anonymous"

  def readFlag(flag: Feature, userId: String): F[Boolean] = {
    Sync[F].delay(client.isFeatureEnabled(flag.name, userId))
  }

  def readFlag(flag: Feature): F[Boolean] = readFlag(flag, DEFAULT_USER_ID)

  def readBooleanFeatureVariable(flag: BooleanFeatureVariable, userId: String): F[Boolean] =
    Sync[F].delay(client.getFeatureVariableBoolean(flag.feature.name, flag.name, userId))

  def readBooleanFeatureVariable(flag: BooleanFeatureVariable): F[Boolean] =
    readBooleanFeatureVariable(flag, DEFAULT_USER_ID)

  def readDoubleFeatureVariable(flag: DoubleFeatureVariable, userId: String): F[Double] =
    Sync[F].delay(client.getFeatureVariableDouble(flag.feature.name, flag.name, userId))

  def readDoubleFeatureVariable(flag: DoubleFeatureVariable): F[Double] =
    readDoubleFeatureVariable(flag, DEFAULT_USER_ID)

  def readIntegerFeatureVariable(flag: IntegerFeatureVariable, userId: String): F[Integer] =
    Sync[F].delay(client.getFeatureVariableInteger(flag.feature.name, flag.name, userId))

  def readIntegerFeatureVariable(flag: IntegerFeatureVariable): F[Integer] =
    readIntegerFeatureVariable(flag, DEFAULT_USER_ID)

  def readStringFeatureVariable(flag: StringFeatureVariable, userId: String): F[String] =
    Sync[F].delay(client.getFeatureVariableString(flag.feature.name, flag.name, userId))

  def readStringFeatureVariable(flag: StringFeatureVariable): F[String] =
    readStringFeatureVariable(flag, DEFAULT_USER_ID)
}

object FeatureFlagReader {
  def apply[F[_]: Sync](sdkKey: String): Stream[F,FeatureFlagReader[F]] = {
    FeatureFlagService[F](sdkKey).map(client => new FeatureFlagReader(client))
  }
}
