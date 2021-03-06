package se.seb.incorrectpasswordblocker.model;

import static se.seb.incorrectpasswordblocker.utils.Constants.MAXIMUM_LOG_IN_ATTEMPTS;

import java.time.Instant;

public class User {

  private String username;
  private String password;
  private Instant firstLoginAttemptDate;
  private Instant lastLoginAttempt;
  private Integer logInAttempts = 0;
  private Boolean accountLocked = false;

  public User(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Instant getFirstLoginAttemptDate() {
    return firstLoginAttemptDate;
  }

  public void setFirstLoginAttemptDate(Instant firstLoginAttemptDate) {
    this.firstLoginAttemptDate = firstLoginAttemptDate;
  }

  public Instant getLastLoginAttempt() {
    return lastLoginAttempt;
  }

  public void setLastLoginAttempt(Instant lastLoginAttempt) {
    this.lastLoginAttempt = lastLoginAttempt;
  }

  public Integer getLogInAttempts() {
    return logInAttempts;
  }

  public void increaseLoginAttemptCount() {
    ++this.logInAttempts;

    if (this.logInAttempts == MAXIMUM_LOG_IN_ATTEMPTS) {
      this.accountLocked = true;
    }
  }

  public void resetLoginAttemptCount(){
    this.logInAttempts = 0;
  }

  public Boolean isAccountLocked() {
    return accountLocked;
  }

  public void unlockAccount(){
    this.accountLocked = false;
  }

}
