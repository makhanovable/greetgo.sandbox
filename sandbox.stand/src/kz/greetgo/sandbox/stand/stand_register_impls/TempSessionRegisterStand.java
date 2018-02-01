package kz.greetgo.sandbox.stand.stand_register_impls;

import kz.greetgo.depinject.core.Bean;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.util.Base64Util;
import kz.greetgo.sandbox.controller.errors.AuthError;
import kz.greetgo.sandbox.controller.register.TempSessionRegister;
import kz.greetgo.sandbox.db.stand.beans.StandDb;
import kz.greetgo.sandbox.db.stand.model.ReportSessionDot;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;

@Bean
public class TempSessionRegisterStand implements TempSessionRegister {

  public BeanGetter<StandDb> db;

  @Override
  public String save(String personId, String urlPath, long lifetimeSecs) {
    SecureRandom secureRandom = new SecureRandom();
    byte[] token = new byte[16];
    secureRandom.nextBytes(token);
    String base64Token = Base64Util.bytesToBase64(token);

    ReportSessionDot reportSessionDot = new ReportSessionDot();
    reportSessionDot.token = base64Token;
    reportSessionDot.urlPath = urlPath;
    reportSessionDot.lifetime = Timestamp.from(Instant.now().plusSeconds(lifetimeSecs));
    reportSessionDot.personId = personId;

    db.get().reportSessionStorage.put(base64Token, reportSessionDot);

    return base64Token;
  }

  @Override
  public void checkForValidity(String token, String urlPath) {
    ReportSessionDot reportSessionDot = db.get().reportSessionStorage.get(token);
    if (reportSessionDot == null) throw new AuthError("Invalid token");
    if (!reportSessionDot.urlPath.equals(urlPath)) throw new AuthError("Forbidden path");
    if (Timestamp.from(Instant.now()).after(reportSessionDot.lifetime)) throw new AuthError("Time expired");
  }

  @Override
  public String remove(String token) {
    String personId = db.get().reportSessionStorage.get(token).personId;
    db.get().reportSessionStorage.remove(token);
    return personId;
  }
}
