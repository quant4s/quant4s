/**
  *
  */
package quanter.rest

/**
  * 回测报告测试
  */
class BackTestServiceSpec extends RoutingSpec with StrategyService{
  implicit def actorRefFactory = system


  "获取回测报告是否正常" should {
    "策略测试 " in {
      // 创建一个策略
      // 执行策略
      //
      success
    }

    "策略测试报告获取" in {
      success
    }
  }

}
