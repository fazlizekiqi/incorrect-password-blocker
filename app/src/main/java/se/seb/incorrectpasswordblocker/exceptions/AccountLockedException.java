package se.seb.incorrectpasswordblocker.exceptions;

public class AccountLockedException extends Exception {

  public AccountLockedException(String message) {
    super(message);
  }
}
