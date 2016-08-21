/**
  *
  */
package quanter.rest
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith


/**
  * 回测报告测试
  */
@RunWith(classOf[JUnitRunner])
class BackTestServiceSpec extends RoutingSpec with StrategyService{
  implicit def actorRefFactory = system
  
  "获取回测报告是否正常" - {
    "策略测试 " in {
      // 创建一个策略
      // 执行策略
      //

    }

    "策略测试报告获取" in {

    }
  }

}
