package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.core.Letter;
import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;

public class SendEmailServiceImpl implements SendEmailService {
  private final DirectEmailSendBuilder builder;

  public SendEmailServiceImpl(DirectEmailSendBuilder builder) {
    this.builder = builder;
  }

  @Override
  public Letter newLetter() {
    return new LetterImpl(builder);
  }
}
