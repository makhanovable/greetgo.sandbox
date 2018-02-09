package kz.greetgo.sandbox.db.stand.tools;

import kz.greetgo.depinject.core.Bean;

import java.util.Random;

@Bean
public class IdGenerator {

  private static final String ENG = "abcdefghijklmnopqrstuvwxyz";
  private static final String DEG = "0123456789";
  private static final char[] ID_CHARS = (ENG + ENG.toLowerCase() + DEG).toCharArray();

  private final Random random = new Random();

  public String newId() {
    char ret[] = new char[19];
    for (int i = 0; i < ret.length; i++) {
      ret[i] = ID_CHARS[random.nextInt(ID_CHARS.length)];
    }
    return new String(ret);
  }
}
