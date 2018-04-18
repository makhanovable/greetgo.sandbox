package kz.greetgo.sandbox.db.util.email_direct_sender;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class DirectEmailSender {

  public static void main(String[] args) {
    new DirectEmailSender().exec();
  }

  public void exec() {
    EmailInfo c = EmailInfo.load();

    Properties properties = new Properties();

    properties.setProperty("mail.smtp.host", "smtp.gmail.com");
    properties.setProperty("mail.smtp.port", "587");
    properties.setProperty("mail.smtp.auth", "true");
    properties.setProperty("mail.smtp.starttls.enable", "true");
    properties.setProperty("mail.mime.charset", "UTF-8");

    Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(c.emailAccountName(), c.emailAccountPassword());
      }
    });

    try {
      MimeMessage message = new MimeMessage(session);

      message.addRecipient(Message.RecipientType.TO, new InternetAddress(c.toEmail1()));
      message.addRecipient(Message.RecipientType.CC, new InternetAddress(c.toEmail2()));
      message.addRecipient(Message.RecipientType.BCC, new InternetAddress(c.toEmail3()));

      message.setSubject("Привет от прекрасного далёка");

      message.setContent(

        "This is <b>красное</b> " +
          "<span style='color:red;font-weight:bold'>сообщение</span>. Hi all." +
          " Привет всем. <i>Это текст курсивом</i>"

        , "text/html; charset=UTF-8");

      message.setText(

        "This is красное " +
          "сообщение. Hi all." +
          " Привет всем. Это текст курсивом"

        , "UTF-8");

      Transport.send(message);
      System.out.println("Sent message successfully....");
    } catch (MessagingException mex) {
      throw new RuntimeException(mex);
    }
  }
}
