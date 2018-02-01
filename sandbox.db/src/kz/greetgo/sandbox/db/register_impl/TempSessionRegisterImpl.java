package kz.greetgo.sandbox.db.register_impl;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.mvc.util.Base64Util;
import kz.greetgo.sandbox.controller.register.TempSessionRegister;

import java.security.SecureRandom;

@Bean
public class TempSessionRegisterImpl implements TempSessionRegister {
  @Override
  public String save(String personId, String urlPath, long lifetimeSecs) {
    SecureRandom secureRandom = new SecureRandom();
    byte[] token = new byte[16];
    secureRandom.nextBytes(token);
    String base64Token = Base64Util.bytesToBase64(token);
/*
    ReportSessionDot reportSessionDot = new ReportSessionDot();
    reportSessionDot.token = base64Token;
    reportSessionDot.urlPath = urlPath;
    reportSessionDot.lifetime = Timestamp.from(Instant.now().plusSeconds(60));
    reportSessionDot.personId = personId;

    db.get().reportSessionStorage.put(base64Token, reportSessionDot);
*/


    return base64Token;
  }

  @Override
  public void checkForValidity(String token, String urlPath) {

  }

  @Override
  public String remove(String token) {
    return null;
  }
}
