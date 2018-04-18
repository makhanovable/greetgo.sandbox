package kz.greetgo.sandbox.stand.launchers;

import kz.greetgo.depinject.Depinject;
import kz.greetgo.depinject.gen.DepinjectUtil;
import kz.greetgo.sandbox.controller.util.Modules;
import kz.greetgo.sandbox.stand.bean_containers.StandBeanContainer;

public class LaunchStandServer {
  public static void main(String[] args) throws Exception {
    new LaunchStandServer().run();
  }

  private void run() throws Exception {
    DepinjectUtil.implementAndUseBeanContainers(
      "kz.greetgo.sandbox.stand",
      Modules.standDir() + "/builder/src_bean_containers");

    StandBeanContainer container = Depinject.newInstance(StandBeanContainer.class);

    container.server().start().join();
  }
}
