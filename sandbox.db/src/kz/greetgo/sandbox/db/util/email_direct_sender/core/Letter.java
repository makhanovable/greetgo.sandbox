package kz.greetgo.sandbox.db.util.email_direct_sender.core;

public interface Letter {
  Letter setTitle(String title);

  Letter addDestination(String email);

  Letter addCopyTo(String email);

  Letter addHiddenCopyTo(String email);

  Letter setContentHtml(String contentHtml);

  Letter setContentText(String contentText);

  void send();
}
