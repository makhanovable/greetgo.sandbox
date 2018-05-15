package kz.greetgo.sandbox.controller.register.auth.model;

import java.io.Serializable;
import java.util.Date;

public class SessionInfo implements Serializable {
  public final String personId;
  public final Date createdAt = new Date();

  public SessionInfo(String personId) {
    if (personId == null) throw new NullPointerException("personId == null");
    if (personId.length() == 0) throw new IllegalArgumentException("personId is empty");
    this.personId = personId;
  }

  @Override
  public String toString() {
    return "SessionInfo{" +
      "personId='" + personId + '\'' +
      ", createdAt=" + createdAt +
      '}';
  }
}
