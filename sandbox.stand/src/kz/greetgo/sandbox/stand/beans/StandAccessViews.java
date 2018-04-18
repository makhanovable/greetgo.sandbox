package kz.greetgo.sandbox.stand.beans;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.sandbox.controller.util.SandboxViews;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static kz.greetgo.util.ServerUtil.streamToStr0;

@Bean
public class StandAccessViews extends SandboxViews {

  //see --> В этом файле можно настраивать скорость работы сервисов (он работает на горячюю)
  private final File config = new File("builder/sleep_in_request.txt");

  @Override
  protected void beforeRequest() {
    sleep();
  }

  private void sleep() {
    if (config.exists()) {
      readAndSleep();
      return;
    }

    {
      config.getParentFile().mkdirs();

      try (PrintWriter pr = new PrintWriter(config)) {
        pr.println("0");
        pr.println("#1500");
        pr.println("#0");
        pr.println("#3000");
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }

    readAndSleep();
  }

  private final AtomicInteger nextIndex = new AtomicInteger(0);

  private void readAndSleep() {
    try {
      List<Long> times
        = Arrays.stream(streamToStr0(new FileInputStream(config)).split("\n"))
        .map(String::trim)
        .filter(s -> !s.startsWith("#"))
        .filter(s -> s.length() > 0)
        .map(Long::parseLong)
        .collect(Collectors.toList());

      if (times.size() == 0) return;

      if (times.size() == 1) {
        sleepLong(times.get(0));
      } else {
        sleepLong(times.get(nextIndex.getAndIncrement() % times.size()));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void sleepLong(long timeToSleep) throws InterruptedException {
    if (timeToSleep <= 0) return;
    Thread.sleep(timeToSleep);
  }
}
