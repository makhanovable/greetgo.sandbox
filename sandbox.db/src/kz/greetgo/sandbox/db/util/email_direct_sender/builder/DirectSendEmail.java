package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;

public class DirectSendEmail {

  String emailAccountName;
  String emailAccountPassword;

  private DirectSendEmail() {}

  public static DirectSendEmail newBuilder() {
    return new DirectSendEmail();
  }

  public DirectSendEmail setGoogleAccount(String emailAccountName, String emailAccountPassword) {
    this.emailAccountName = emailAccountName;
    this.emailAccountPassword = emailAccountPassword;
    return this;
  }

  public SendEmailService build() {
    return new SendEmailServiceImpl(this);
  }
}
