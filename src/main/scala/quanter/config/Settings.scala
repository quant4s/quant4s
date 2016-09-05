/**
  *
  */
package quanter.config

import akka.actor.ActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.actor.ExtendedActorSystem
import scala.concurrent.duration.Duration
import com.typesafe.config.Config
import java.util.concurrent.TimeUnit

/**
  *
  */
class Quant4sSettings(config: Config) extends Extension{
  val real_dataprovider: String = config.getString("quant4s.real.dataprovider")
  val real_path: String = config.getString("quant4s.real.path")
}

object Settings extends ExtensionId[Quant4sSettings] with ExtensionIdProvider {
  override def lookup = Settings

  override def createExtension(system: ExtendedActorSystem) = new Quant4sSettings(system.settings.config)

  override def get(system: ActorSystem): Quant4sSettings = super.get(system)
}