package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.EmailInfo;
import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;
import org.testng.annotations.Test;

public class DirectSendEmailTest {

  @Test
  public void testName() throws Exception {
    EmailInfo c = EmailInfo.load();

    SendEmailService sendEmailService = DirectSendEmail.newBuilder()
      .setGoogleAccount(c.googleAccountName(), c.googleAccountPassword())
      .build();

    sendEmailService.newLetter()
      .setTitle("Приветствие 1")
      .addDestination(c.toEmail1())
      .addCopyTo(c.toEmail2())
      .addHiddenCopyTo(c.toEmail3())
      .setContentHtml("Это тело <b>письма в виде</b> HTML-а")
      .send()
    ;

    sendEmailService.newLetter()
      .setTitle("Приветствие 2")
      .addDestination(c.toEmail1())
      .addCopyTo(c.toEmail2())
      .addHiddenCopyTo(c.toEmail3())
      .setContentText("Это тело письма в виде текста")
      .send()
    ;
  }
}