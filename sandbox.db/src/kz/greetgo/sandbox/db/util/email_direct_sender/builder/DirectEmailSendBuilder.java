package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;

public class DirectEmailSendBuilder {
  String emailAccountName;
  String emailAccountPassword;

  private DirectEmailSendBuilder() {}

  public static DirectEmailSendBuilder newBuilder() {
    return new DirectEmailSendBuilder();
  }

  boolean accountDefined = false;

  public DirectEmailSendBuilder setGoogleAccount(String emailAccountName, String emailAccountPassword) {
    if (accountDefined) throw new RuntimeException("Аккаутн уже определён");
    accountDefined = true;
    this.emailAccountName = emailAccountName;
    this.emailAccountPassword = emailAccountPassword;
    return this;
  }

  public SendEmailService build() {
    return new SendEmailServiceImpl(this);
  }
}
