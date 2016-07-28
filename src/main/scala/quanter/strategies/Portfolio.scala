/**
  *
  */
package quanter.strategies

/**
  *
  */
class Portfolio {
  var cash: Double = 0
  var availableCash: Double = 0
  var frozenCash: Double = 0

  private val _stockFee: Double = 0.0002
  private val _fundFee: Double = 0.0001
  private val _tax = 0.001

  def fee(symbol: String): Double = {
    _stockFee
  }


  /**
    * 提交一个委托，计算冻结资金，
    */
  def buy(symbol: String, price: Double, quantity: Int): Unit = {
    // 计算资金
    val buyCash = price * quantity
    val buyFee = buyCash * fee(symbol)
    val subCash = (buyCash + buyFee)
    frozenCash += subCash
    availableCash -= subCash

    // 生成新的委托记录
//    val order =
  }
}
