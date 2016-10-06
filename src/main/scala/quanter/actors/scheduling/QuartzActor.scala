/**
  *
  */
package quanter.actors.scheduling

import java.util.{HashMap, Properties}

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, ActorSelection, Props}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import quanter.config.Settings

/**
  * 读取配置文件，将加入到Job队列, Jobf分为两种 Actor, 消息为ExecuteJob， 普通Job
  */
class QuartzActor extends Actor with ActorLogging{

  val scheduler = _createScheduler
  _initJobs

  override def receive: Receive = {
    case AddCronSchedule(name, cron, to, trigger) ⇒
  }

  private def _createScheduler()= {
    val props = new Properties()
    props.setProperty(StdSchedulerFactory.PROP_SCHED_INSTANCE_NAME, context.self.path.name)
    props.setProperty("org.quartz.threadPool.threadCount", "2")
    props.setProperty(StdSchedulerFactory.PROP_JOB_STORE_CLASS, "org.quartz.simpl.RAMJobStore")
    props.setProperty(StdSchedulerFactory.PROP_SCHED_SKIP_UPDATE_CHECK, "true")
    new StdSchedulerFactory(props).getScheduler
  }

  /**
    * 读取配置文件
    */
  private def _initJobs(): Unit = {

    val setting = Settings(context.system)
    val jobDetails = setting.getAnyRefList("akka.quartz.jobs")
//    val jobDetails = List[JobDetail]()
    for(i <- 0 until jobDetails.size()) {
      val provider = jobDetails.get(i)

      val name = (provider.asInstanceOf[HashMap[String, String]]).get("name")
      val cron = (provider.asInstanceOf[HashMap[String, String]]).get("cron")
      val clazz =(provider.asInstanceOf[HashMap[String, String]]).get("clazz")
      val actorSelection =(provider.asInstanceOf[HashMap[String, String]]).get("actorSelection")
      val trigger =(provider.asInstanceOf[HashMap[String, String]]).get("trigger")

      val jdm = new JobDataMap()
      if(actorSelection != null) {
        jdm.put("actor", actorSelection)
        jdm.put("actorContext", context)
      }

      val job = (if(actorSelection != null) JobBuilder.newJob(classOf[QuartzIsNotScalaExecutor])
                else JobBuilder.newJob(Class.forName(clazz).asSubclass(classOf[Job]))).usingJobData(jdm).withIdentity(name).build()
      val trigKey = new TriggerKey("trigger_" + trigger)

      try {
        scheduler.scheduleJob(job, TriggerBuilder.newTrigger().startNow()
          .withIdentity(trigKey).forJob(job)
          .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build())
        scheduler.startDelayed(3)
      } catch {
        case e: Exception ⇒  log.error("Quartz failed to add a task: ", e)
      }
    }

  }
}

object QuartzActor {
  def props = {
    Props.create(classOf[QuartzActor])
  }

  val path = "quartz"
}

case class AddCronSchedule(name: String,  cron: String, to: ActorSelection, trigger: String)
case class JobDetail(name: String, cron: String, trigger: String, clazz: Option[String], actorSelection: Option[String])
case class ExecuteJob()

class QuartzIsNotScalaExecutor extends Job {
  override def execute(context: JobExecutionContext): Unit = {
    val jdm = context.getJobDetail().getJobDataMap
    val ctx = jdm.get("actorContext").asInstanceOf[ActorContext]
    val actor = ctx.actorSelection(jdm.get("actor").toString)
    if(actor != null)
      actor ! new ExecuteJob()
  }
}

