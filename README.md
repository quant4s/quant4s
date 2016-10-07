
# quant4s
一个用scala 实现的交易平台系统，支持多语言（C#, PYTHON, JAVA, R, MATLAB）编写策略，支持实盘

# 简介
引擎采用Scala 语言，基于Akka 架构编写，并完全采用开源化管理方案。系统可以构建在桌面系统和云服务器上。
设计目标是采用Actor模型，提供一种大数据下，高并发计算的股票算法交易方案。系统采用JDK1.8，支持LINUX, WINDOWS, MAC OS等平台。
引擎分为服务器端和客户端两个部分组成。客户端请参见[quant4s-sdk][quant4s-sdk-href]。

quant4s 可以胜任绝大多数的策略应用场合，但是对ms级别的高频有点力不从心，除此之外的策略，quant4s 都游刃有余。

更详细的内容参见[WIKI][quant4s-wiki-href]

# 技术架构
参见知乎

# 技术特点
1. 采用了分布式部署，将策略和引擎从物理层上分离。引擎部署在服务器端，策略部署在客户端，使得执行多个策略成为可能。
2. 采用Actor模型，实现了高并发的计算，让多指标、大数据量计算不在成为性能的瓶颈
1. 将指标的计算从客户端转移到服务器端，在应用层实现了指标的共享，降低了策略开发的难度
3. 优雅的实现了跨周期策略的直接支持, 可以直接指定技术指标的数据推送的周期，参见SDK
5. 完美的提供了多语言的支持。只要语言可以访问http，可以支持zeromq，那么这种语言就可以用来编写策略
6. 优美的指标框架，可以很方便的扩展自己的技术指标
7. 提供了作业机制，可用于定期作业。比如（每天早上 9：00 推送历史数据分钟线，保证当天计算技术指标的连续性

# 后续的目标
1. 集成选股框架，让策略选股更容易
2. 完善系统监控界面
3. 


# 部署方法
1. 安装zeroMQ， 参见 http://zeromq.org/ ,
2. 克隆，编译，运行项目


 ```
 git clone https://github.com/quant4s/quant4s
 mvn compile
 sh run.sh [linux]
 ```

# 扩展方法
## 交易接口
1. 从Brokerage派生之类
2. 修改TradeRouteActor._init, 增加支持

## 数据接口
1. 参照 SinaL1Actor, 构建Actor
2. 修改配置文件application.conf, 可同时支持多个数据源（不同的数据， 如果数据相同，在Tick级别时可能会报错）。 

[quant4s-sdk-href]: https://github.com/quant4s/quant4s-sdk "SDK"
[quant4s-wiki-href]: https://github.com/quant4s/quant4s/wiki "wiki"
