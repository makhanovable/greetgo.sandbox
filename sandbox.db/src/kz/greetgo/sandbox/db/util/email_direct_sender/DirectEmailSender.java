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
    Properties properties = new Properties();

    properties.setProperty("mail.smtp.host", "smtp.gmail.com");
    properties.setProperty("mail.smtp.port", "587");
    properties.setProperty("mail.smtp.auth", "true");
    properties.setProperty("mail.smtp.starttls.enable", "true");
    properties.setProperty("mail.mime.charset", "UTF-8");

    Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication("jkolpakov@gmail.com", "jkolpakov123");
      }
    });

    try {
      MimeMessage message = new MimeMessage(session);

      message.addRecipient(Message.RecipientType.TO, new InternetAddress("ekolpakov@greet-go.com"));
      message.addRecipient(Message.RecipientType.CC, new InternetAddress("john.kolpakov.x@gmail.com"));
      message.addRecipient(Message.RecipientType.BCC, new InternetAddress("pompei@mail.ru"));

      message.setSubject("Привет от прекрасного далёка");

      message.setContent(

        "This is <b>красное</b> " +
          "<span style='color:red;font-weight:bold'>сообщение</span>. Hi all." +
          " Привет всем. <i>Это текст курсивом</i>"

        , "text/html; charset=UTF-8");

      Transport.send(message);
      System.out.println("Sent message successfully....");
    } catch (MessagingException mex) {
      throw new RuntimeException(mex);
    }
  }
}
