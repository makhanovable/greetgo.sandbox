package kz.greetgo.sandbox.db.util.email_direct_sender.builder;

import kz.greetgo.sandbox.db.util.email_direct_sender.core.Letter;

public class LetterImpl implements Letter {
  @Override
  public Letter setTitle(String title) {
    return this;
  }

  @Override
  public Letter addDestination(String email) {
    return this;
  }

  @Override
  public Letter addCopyTo(String email) {
    return this;
  }

  @Override
  public Letter addHiddenCopyTo(String email) {
    return this;
  }

  @Override
  public Letter setContentHtml(String htmlContent) {
    return this;
  }

  @Override
  public Letter setContentText(String textContent) {
    return this;
  }

  @Override
  public void send() {

  }
}
