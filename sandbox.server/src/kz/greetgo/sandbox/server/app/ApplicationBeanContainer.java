package kz.greetgo.sandbox.server.app;

import kz.greetgo.depinject.core.BeanContainer;
import kz.greetgo.depinject.core.Include;
import kz.greetgo.sandbox.server.beans.AppInitializer;

@Include(BeanConfigApplication.class)
public interface ApplicationBeanContainer extends BeanContainer {
  AppInitializer appInitializer();

}
