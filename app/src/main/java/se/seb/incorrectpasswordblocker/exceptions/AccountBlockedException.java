package se.seb.incorrectpasswordblocker.exceptions;

public class AccountBlockedException extends Exception {

  public AccountBlockedException(String message) {
    super(message);
  }
}
