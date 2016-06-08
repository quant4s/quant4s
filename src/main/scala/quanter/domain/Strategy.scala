package quanter.domain


class Strategy(val id: Long) {
  var name = "unnamed"
  var lang = "c#"
  var runMode = 1    //
  var tradeRoute = 1  // 交易路由

  var portfolio = new Portfolio()
}
