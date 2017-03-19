/**
  *
  */
package org.quant4s.schedule.daily

import org.quartz.{Job, JobExecutionContext}
import org.quant4s.interfaces.TLogging

/**
  * 1. 获取股票日线数据
  * 2. 计算 股票的数据 MA5， MA10, MA15
  * 3. 计算
  */
class FetchStockDailyDataJob extends Job with TLogging {
  override def execute(context: JobExecutionContext): Unit = {
    // 1. 访问sina 数据，保存到文本文件

    // 2. 订阅日线指标, 数据推送过来的时候,写到文件

    // 3.
  }
}
