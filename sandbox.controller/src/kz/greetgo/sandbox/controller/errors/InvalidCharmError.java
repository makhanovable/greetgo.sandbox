package kz.greetgo.sandbox.controller.errors;

public class InvalidCharmError extends RestError {

  public InvalidCharmError(int statusCode, String message) {
    super(statusCode, message);
  }
}
