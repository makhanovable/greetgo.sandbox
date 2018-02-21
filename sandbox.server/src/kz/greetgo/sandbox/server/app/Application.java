package kz.greetgo.sandbox.server.app;

import kz.greetgo.depinject.Depinject;
import kz.greetgo.sandbox.db.register_impl.migration.MigrationController;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class Application implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    ApplicationBeanContainer appBeanContainer;

    try {
      appBeanContainer = Depinject.newInstance(ApplicationBeanContainer.class);
      appBeanContainer.appInitializer().initialize(ctx);
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      if (e instanceof ServletException) throw (ServletException) e;
      throw new RuntimeException(e);
    }

    this.launchMigrationScheduler(appBeanContainer);
  }

  private void launchMigrationScheduler(ApplicationBeanContainer appBeanContainer) {
    JobDetail jobDetail = JobBuilder.newJob(MigrationController.class)
      .withIdentity("job", "migration")
      .build();

    Trigger trigger = TriggerBuilder.newTrigger()
      .withIdentity("trigger", "migration")
      .startNow()
      .withSchedule(
        SimpleScheduleBuilder.simpleSchedule()
          .withIntervalInSeconds(10)
          .repeatForever()
      )
      .forJob(jobDetail)
      .build();

    SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    Scheduler scheduler;

    try {
      scheduler = schedulerFactory.getScheduler();
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }

    try {
      scheduler.scheduleJob(jobDetail, trigger);
      scheduler.start();
    } catch (SchedulerException e) {
      throw new RuntimeException(e);
    }
  }
}
