package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.core.Letter;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LetterImpl implements Letter {
  private DirectEmailSendBuilder builder;
  private String title;

  public LetterImpl(DirectEmailSendBuilder builder) {
    this.builder = builder;
  }

  @Override
  public Letter setTitle(String title) {
    if (title == null) throw new IllegalArgumentException("title cannot be null");
    if (this.title != null) throw new RuntimeException("Title already defined");
    this.title = title;
    return this;
  }

  private final List<String> destinationList = new ArrayList<>();
  private final List<String> copyToList = new ArrayList<>();
  private final List<String> hiddenCopyToList = new ArrayList<>();

  @Override
  public Letter addDestination(String email) {
    checkEmailFormat(email);
    destinationList.add(email);
    return this;
  }

  @Override
  public Letter addCopyTo(String email) {
    checkEmailFormat(email);
    copyToList.add(email);
    return this;
  }

  @Override
  public Letter addHiddenCopyTo(String email) {
    checkEmailFormat(email);
    hiddenCopyToList.add(email);
    return this;
  }

  private void checkEmailFormat(String email) {
    if (!email.contains("@")) throw new RuntimeException("Not email: " + email);
  }

  private boolean contentWasDefined = false;
  private String htmlContent = null;

  @Override
  public Letter setContentHtml(String htmlContent) {
    if (contentWasDefined) throw new RuntimeException("Content already defined");
    this.htmlContent = htmlContent;
    contentWasDefined = true;
    return this;
  }

  private String textContent = null;

  @Override
  public Letter setContentText(String textContent) {
    if (contentWasDefined) throw new RuntimeException("Content already defined");
    this.textContent = textContent;
    contentWasDefined = true;
    return this;
  }

  @Override
  public void send() {

    if (destinationList.isEmpty()) throw new RuntimeException("No destination");
    if (title == null) throw new RuntimeException("No title");
    if (!contentWasDefined) throw new RuntimeException("No content");

    Properties properties = new Properties();

    properties.setProperty("mail.smtp.host", "smtp.gmail.com");
    properties.setProperty("mail.smtp.port", "587");
    properties.setProperty("mail.smtp.auth", "true");
    properties.setProperty("mail.smtp.starttls.enable", "true");
    properties.setProperty("mail.mime.charset", "UTF-8");

    Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
      @Override
      protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(builder.emailAccountName, builder.emailAccountPassword);
      }
    });

    try {
      MimeMessage message = new MimeMessage(session);

      for (String email : destinationList) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
      }
      for (String email : copyToList) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(email));
      }
      for (String email : hiddenCopyToList) {
        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
      }

      message.setSubject(title);

      if (htmlContent != null) {
        message.setContent(htmlContent, "text/html; charset=UTF-8");
      }

      if (textContent != null) {
        message.setText(textContent, "UTF-8");
      }

      Transport.send(message);
    } catch (MessagingException mex) {
      throw new RuntimeException(mex);
    }
  }
}
