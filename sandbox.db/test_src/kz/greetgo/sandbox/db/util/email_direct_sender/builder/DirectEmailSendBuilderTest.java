package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.EmailInfo;
import kz.greetgo.sandbox.db.util.email_direct_sender.core.SendEmailService;
import org.testng.annotations.Test;

public class DirectEmailSendBuilderTest {

  @Test
  public void testName() throws Exception {
    EmailInfo c = EmailInfo.load();

    SendEmailService sendEmailService = DirectEmailSendBuilder.newBuilder()
      .setAccount(c.emailAccountName(), c.emailAccountPassword())
      .build();

    sendEmailService.newLetter()
      .setTitle("Заголовок письма")
      .addDestination(c.toEmail1())
      .addCopyTo(c.toEmail2())
      .addHiddenCopyTo(c.toEmail3())
      .setContentHtml("Привем все <b>Вам</b>")
      .send()
    ;

    sendEmailService.newLetter()
      .setTitle("Заголовок письма")
      .addDestination(c.toEmail1())
      .addCopyTo(c.toEmail2())
      .addHiddenCopyTo(c.toEmail3())
      .setContentText("Привем все Вам")
      .send()
    ;

    System.out.println(sendEmailService);
  }
}