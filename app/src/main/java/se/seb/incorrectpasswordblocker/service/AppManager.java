package se.seb.incorrectpasswordblocker.service;

import static se.seb.incorrectpasswordblocker.utils.Constants.ACCOUNT_IS_LOCKED_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.CREDENTIALS_CANNOT_BE_NULL_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.FIVE_MINUTES_IN_SECONDS;
import static se.seb.incorrectpasswordblocker.utils.Constants.GET_PASSWORD_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.GET_USERNAME_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.MINUTE_IN_SECONDS;
import static se.seb.incorrectpasswordblocker.utils.Constants.USERNAME_NOT_FOUND_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.WRONG_CREDENTIALS_MESSAGE;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import se.seb.incorrectpasswordblocker.db.DatabaseSimulator;
import se.seb.incorrectpasswordblocker.exceptions.AccountLockedException;
import se.seb.incorrectpasswordblocker.exceptions.UserNotFoundException;
import se.seb.incorrectpasswordblocker.exceptions.WrongCredentialsException;
import se.seb.incorrectpasswordblocker.model.User;
import se.seb.incorrectpasswordblocker.model.UserCredentials;
import se.seb.incorrectpasswordblocker.utils.InputUtil;

public class AppManager {

  private final InputUtil inputUtil;
  private final DatabaseSimulator dbSimulator;

  public AppManager(InputUtil inputUtil, DatabaseSimulator dbSimulator) {
    this.inputUtil = Objects.requireNonNull(inputUtil);
    this.dbSimulator = Objects.requireNonNull(dbSimulator);
  }

  public UserCredentials getUserCredentials() {
    System.out.println(GET_USERNAME_MESSAGE);
    String username = inputUtil.readLine();
    System.out.println(GET_PASSWORD_MESSAGE);
    String password = inputUtil.readLine();

    UserCredentials userCredentials = new UserCredentials(username, password);

    return userCredentials;
  }

  public void authenticate(UserCredentials userCredentials)
    throws UserNotFoundException, AccountLockedException, WrongCredentialsException {

    UserCredentials credentials = Objects.requireNonNull(userCredentials, CREDENTIALS_CANNOT_BE_NULL_MESSAGE);

    User user = dbSimulator
      .findUserByUsername(userCredentials.username())
      .orElseThrow(() -> new UserNotFoundException(String.format(USERNAME_NOT_FOUND_MESSAGE, credentials.username())));

    Instant lastLoginAttempt = user.getLastLoginAttempt();
    if (lastLoginAttempt != null) {

      if (user.isAccountLocked()) {

        boolean lessThenFiveMinutesHaveGone = hasLessThanFiveMinutesGoneSinceLastLoginAttempt(lastLoginAttempt);
        if (lessThenFiveMinutesHaveGone) {
          throw new AccountLockedException(ACCOUNT_IS_LOCKED_MESSAGE);
        }

        resetTheUserState(user);
      }
    }

    verifyPasswords(credentials, user);

    // User is authenticated -> something can be returned either a boolean or an Authenticate object.
    resetTheUserState(user);
  }

  private void verifyPasswords(UserCredentials credentials, User user) throws WrongCredentialsException {

    boolean isWrongPassword = !Objects.equals(user.getPassword(), credentials.password());
    if (isWrongPassword) {

      boolean isFirstAttempt = checkForFirstLoginAttempt(user);
      if (isFirstAttempt) {
        user.setFirstLoginAttemptDate(Instant.now());
      }

      boolean moreThanOneMinuteHasGone = hasMoreThanMinuteGoneSinceFirstAttempt(user);
      if (moreThanOneMinuteHasGone) {
        user.setFirstLoginAttemptDate(Instant.now());
        user.resetLoginAttemptCount();
      }

      user.increaseLoginAttemptCount();
      user.setLastLoginAttempt(Instant.now());
      throw new WrongCredentialsException(WRONG_CREDENTIALS_MESSAGE);

    }
  }

  private boolean hasLessThanFiveMinutesGoneSinceLastLoginAttempt(Instant lastLoginAttempt) {
    long minutesSinceLastAttempt = lastLoginAttempt.until(Instant.now(), ChronoUnit.SECONDS);

    return minutesSinceLastAttempt < FIVE_MINUTES_IN_SECONDS;
  }

  private boolean hasMoreThanMinuteGoneSinceFirstAttempt(User user) {
    Instant firstLoginAttemptDate = user.getFirstLoginAttemptDate();
    long passedSeconds = firstLoginAttemptDate.until(Instant.now(), ChronoUnit.SECONDS);

    return passedSeconds > MINUTE_IN_SECONDS;
  }

  private boolean checkForFirstLoginAttempt(User user) {
    return user.getFirstLoginAttemptDate() == null;
  }

  private void resetTheUserState(User user) {
    user.setFirstLoginAttemptDate(null);
    user.setLastLoginAttempt(null);
    user.resetLoginAttemptCount();
    user.unlockAccount();
  }


}
