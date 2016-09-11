/**
  *
  */
package quanter.indicators

import java.util.Date

import quanter.QuanterUnitSpec
import quanter.CommonExtensions._
import quanter.data.market.TradeBar

/**
  *
  */
class StochasticSpec extends QuanterUnitSpec{

  describe("用外部文件测试") {
    it("测试FastStoch"){
      val stochastic = new Stochastic("sto", 12, 3, 5);

      TestHelper.testTradeBarIndicator(stochastic, "spy_with_stoch12k3.txt", "Stochastics12 %K3",
        (ind, expected) => (ind.asInstanceOf[Stochastic]).fastStoch.current.value should be(ind.current.value))
    }

    it("测试 StochasticsK") {
      val stochastic = new Stochastic("sto", 12, 3, 5)
      epsilon = 1e-3

      TestHelper.testTradeBarIndicator(stochastic, "spy_with_stoch12k3.txt", "Stochastics12 %K3",
        (ind, expected) => (ind.asInstanceOf[Stochastic]).stochK.current.value should be(expected +- epsilon))
    }

    it("测试StochD") {
      val stochastics = new Stochastic("sto", 12, 3, 5);
      epsilon = 1e-3
      TestHelper.testTradeBarIndicator(stochastics, "spy_with_stoch12k3.txt", "%D5",
        (ind, expected) => (ind.asInstanceOf[Stochastic]).stochD.current.value should be(expected +- epsilon))
    }
  }

  describe("EqualMinAndMax") {
    it("EqualMinAndMax") {
      val reference = new Date()
      val stochastics = new Stochastic("sto", 2, 2, 2)
      for (i <- 0 until 4) {
        val bar = new TradeBar()
        bar.time = reference.addSeconds(i)
        bar.open = 1.0
        bar.close = 1.0
        bar.high = 1.0
        bar.low = 1.0

        stochastics.update(bar)
        stochastics.current.value should be(0.0)
      }
    }
  }
}
