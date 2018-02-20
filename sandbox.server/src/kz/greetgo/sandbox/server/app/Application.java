package kz.greetgo.sandbox.server.app;

import kz.greetgo.depinject.Depinject;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class Application implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    try {
      ApplicationBeanContainer applicationBeanContainer = Depinject.newInstance(ApplicationBeanContainer.class);

      applicationBeanContainer.appInitializer().initialize(ctx);
      //applicationBeanContainer.
    } catch (Exception e) {
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      if (e instanceof ServletException) throw (ServletException) e;
      throw new RuntimeException(e);
    }
  }
}
