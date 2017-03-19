/**
  *
  */
package org.quant4s.schedule

import org.quartz.{Job, JobExecutionContext}
import org.quant4s.interfaces.TLogging

/**
  *
  */
class TestLogJob extends Job with TLogging{
  override def execute(context: JobExecutionContext): Unit = {
    log.info("测试JOB")
  }
}
