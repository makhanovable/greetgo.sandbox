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
  private DirectSendEmail builder;

  public LetterImpl(DirectSendEmail builder) {
    this.builder = builder;
  }

  private String title;

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
    if (email == null) throw new IllegalArgumentException("email cannot be null");
    destinationList.add(email);
    return this;
  }

  @Override
  public Letter addCopyTo(String email) {
    if (email == null) throw new IllegalArgumentException("email cannot be null");
    copyToList.add(email);
    return this;
  }

  @Override
  public Letter addHiddenCopyTo(String email) {
    if (email == null) throw new IllegalArgumentException("email cannot be null");
    hiddenCopyToList.add(email);
    return this;
  }

  private String contentHtml;
  private boolean contentDefined = false;

  @Override
  public Letter setContentHtml(String contentHtml) {
    if (contentHtml == null) throw new IllegalArgumentException("Content cannot be null");
    if (contentDefined) throw new RuntimeException("Content already defined");
    this.contentHtml = contentHtml;
    contentDefined = true;
    return this;
  }

  private String contentText;

  @Override
  public Letter setContentText(String contentText) {
    if (contentText == null) throw new IllegalArgumentException("Content cannot be null");
    if (contentDefined) throw new RuntimeException("Content already defined");
    this.contentText = contentText;
    contentDefined = true;
    return this;
  }

  @Override
  public void send() {

    if (title == null) throw new RuntimeException("No title");
    if (!contentDefined) throw new RuntimeException("No content");

    if (destinationList.isEmpty()) throw new RuntimeException("No destination");

    Properties props = new Properties();

    props.setProperty("mail.smtp.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.port", "587");
    props.setProperty("mail.smtp.auth", "true");
    props.setProperty("mail.smtp.starttls.enable", "true");
    props.setProperty("mail.mime.charset", "UTF-8");

    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
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

      if (contentHtml != null) {
        message.setContent(contentHtml, "text/html; charset=UTF-8");
      }

      if (contentText != null) {
        message.setText(contentText, "UTF-8");
      }

      Transport.send(message);
    } catch (MessagingException mex) {
      throw new RuntimeException(mex);
    }
  }
}
