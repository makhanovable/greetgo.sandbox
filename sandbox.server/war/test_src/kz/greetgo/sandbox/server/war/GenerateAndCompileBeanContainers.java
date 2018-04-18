package kz.greetgo.sandbox.server.war;

import kz.greetgo.depinject.gen.DepinjectUtil;

public class GenerateAndCompileBeanContainers {
  public static void main(String[] args) throws Exception {
    String outDir = args.length < 1 ? "builder/gen_java_out_dir" : args[0];
    DepinjectUtil.implementBeanContainers("kz.greetgo.sandbox", outDir);
  }
}
