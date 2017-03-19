package org.quant4s.actors.persistence

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.ConfigFactory
import org.quant4s.persistence._

/**
  *
  */
trait BasePersistorActor extends Actor with ActorLogging {
  import profile.simple._

  val config = ConfigFactory.load()
  var db = _getDatabase
  implicit val session = db.createSession()

  def _getDatabase: Database = {
    val TEST = "test"
    val DEV = "dev"
    val PROD = "prod"
    val runMode = config.getString("quant4s.runMode")
    Database.forConfig(runMode)
  }

}
