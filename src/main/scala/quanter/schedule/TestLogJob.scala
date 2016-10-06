/**
  *
  */
package quanter.schedule

import org.quartz.{Job, JobExecutionContext}
import quanter.interfaces.TLogging

/**
  *
  */
class TestLogJob extends Job with TLogging{
  override def execute(context: JobExecutionContext): Unit = {
    logger.info("测试JOB")
  }
}
