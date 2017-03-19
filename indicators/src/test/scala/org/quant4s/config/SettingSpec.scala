/**
  *
  */
package org.quant4s.config

import com.typesafe.config.ConfigFactory
import org.scalatest.{FunSpec, Matchers}

import java.util.HashMap
import scala.collection.mutable
import scala.collection.parallel.immutable

/**
  *
  */
class SettingSpec extends FunSpec with Matchers {
  describe("测试自定义配置项") {
    it("测试") {
      val config = ConfigFactory.load("application.conf")
      val setting = new Quant4sSettings(config)
      setting.providers.size() should be(2)
      setting.channelTypes.size() should be(3)

//      for(i <- 0 until setting.providers.size()) {
//        val provider = setting.providers.get(i)
//        println((provider.asInstanceOf[HashMap[String, String]]).get("name"))
//      }
//      for(provider <- setting.providers) {
//        println("name: %s, provider: ".format(provider))
//      }
    }
  }
}
