package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.EmailInfo;
import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;
import org.testng.annotations.Test;

public class DirectEmailSendBuilderTest {

  @Test
  public void testName() throws Exception {
    EmailInfo c = EmailInfo.load();

    SendEmailService sendEmailService = DirectEmailSendBuilder.newBuilder()
      .setGoogleAccount(c.emailAccountName(), c.emailAccountPassword())
      .build();

    sendEmailService.newLetter()
      .setTitle("Заголовок письма html")
      .addDestination(c.toEmail1())
      .addCopyTo(c.toEmail2())
      .addHiddenCopyTo(c.toEmail3())
      .setContentHtml("Привем всем <b>Вам</b>")
      .send()
    ;

    sendEmailService.newLetter()
      .setTitle("Заголовок письма text")
      .addDestination(c.toEmail1())
      .addCopyTo(c.toEmail2())
      .addHiddenCopyTo(c.toEmail3())
      .setContentText("Привем всем Вам")
      .send()
    ;

    System.out.println(sendEmailService);
  }
}