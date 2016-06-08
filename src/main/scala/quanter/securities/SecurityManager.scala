package quanter.securities

import java.io.File

import com.github.tototoshi.csv.CSVReader

import scala.collection.immutable.HashMap

/**
  *
  */
class SecurityManager extends Map[String, Security]{
  var securityManager = HashMap[String, Security]()
  _init()

  def addSecurity(symbol: String, security: Security): Unit = {
    securityManager += (symbol -> security)
    // TODO: notify changed event
  }

  def addSecurity(security: Security): Unit =  addSecurity(security.symbol, security)
  def addSecurity(pair: (String, Security)): Unit = addSecurity(pair._1, pair._2)

  def removeSecurity(symbol: String): Unit = {
    securityManager.-(symbol)
    // TODO: notify changed event
  }

  def removeSecurity(pair: (String, Security)): Unit = {
    removeSecurity(pair._1)
  }

  private def _init() = {
    // read all securities in file "symbol_list.csv"
    val file = new File("stock_list.csv")
    val reader = CSVReader.open(file)
    val symbols = reader.allWithHeaders()
    symbols.foreach(m => {
      val symbol = m("代码")
      val security = new Security(symbol)
      securityManager += (symbol -> security)
    })
  }

  override def empty = securityManager.empty

  override def contains(symbol: String) = securityManager.contains(symbol)

  override def size = securityManager.size

  override def +[B1 >: Security](kv: (String, B1)): Map[String, B1] = securityManager.+(kv)

  override def get(key: String): Option[Security] = securityManager.get(key)

  override def iterator: Iterator[(String, Security)] = securityManager.iterator

  override def -(key: String): Map[String, Security] = securityManager.-(key)
}
