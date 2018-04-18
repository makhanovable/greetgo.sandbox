package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;

public class DirectEmailSendBuilder {
  private DirectEmailSendBuilder() {}

  public static DirectEmailSendBuilder newBuilder() {
    return new DirectEmailSendBuilder();
  }

  public DirectEmailSendBuilder setAccount(String emailAccountName, String emailAccountPassword) {
    return this;
  }

  public SendEmailService build() {
    return new SendEmailServiceImpl();
  }
}
